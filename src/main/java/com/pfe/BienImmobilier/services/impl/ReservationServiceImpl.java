package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.mapper.ReservationMapper;
import com.pfe.BienImmobilier.model.IndisponibiliteDTO;
import com.pfe.BienImmobilier.model.NotificationDTO;
import com.pfe.BienImmobilier.model.ReservationDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.ReservationRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.inter.EmailService;
import com.pfe.BienImmobilier.services.inter.NotificationService;
import com.pfe.BienImmobilier.services.inter.ReservationService;
import com.pfe.BienImmobilier.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BienImmobilierRepository bienRepository;
    private final UserRepository utilisateurRepository;
    private final JwtUtil jwtService;
    private final HttpServletRequest request;
    private final ReservationMapper reservationMapper;
    private final BienImmobilierRepository bienImmobilierRepository;
    private final EmailService emailService;
    private final NotificationService notificationService; // ✅ Ajout du service de notification

    @Override
    public ReservationDTO creerReservation(Reservation reservation, Long bienId) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        BienImmobilier bien = bienImmobilierRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        reservation.setUtilisateur(utilisateur);
        reservation.setBienImmobilier(bien);
        reservation.setPropietaire(bien.getProprietaire());
        reservation.setStatut(EStatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now());

        // Vérifier les chevauchements
        List<Reservation> reservationsExistantes = reservationRepository
                .findByBienImmobilierIdAndStatut(bienId, EStatutReservation.CONFIRMEE);

        for (Reservation existante : reservationsExistantes) {
            boolean chevauchement = reservation.getDateDebut().isBefore(existante.getDateFin()) &&
                    reservation.getDateFin().isAfter(existante.getDateDebut());
            if (chevauchement) {
                throw new RuntimeException("Le bien est déjà réservé sur cette période.");
            }
        }

        Reservation saved = reservationRepository.save(reservation);

        // ✉️ Email au propriétaire
        String sujet = "Nouvelle réservation reçue";
        String message = "Bonjour " + bien.getProprietaire().getNom() + ",<br><br>" +
                "Vous avez reçu une nouvelle réservation pour votre bien situé à : <strong>" + bien.getAdresse() + "</strong>.<br><br>" +
                "Client : <strong>" + utilisateur.getNom() + " " + utilisateur.getPrenom() + "</strong><br>" +
                "Email du client : <strong>" + utilisateur.getEmail() + "</strong><br><br>" +
                "Veuillez vous connecter pour confirmer ou refuser cette réservation.<br><br>" +
                "Cordialement,<br>L'équipe de Gestion Immobilière";

        emailService.envoyerEmail(bien.getProprietaire().getEmail(), sujet, message);

        // 🔔 Notification propriétaire
        String messageNot = String.format(
                "Nouvelle réservation pour %s par %s %s (Email: %s)",
                bien.getTitre(),
                utilisateur.getPrenom(),
                utilisateur.getNom(),
                utilisateur.getEmail()
        );

        NotificationDTO notification = new NotificationDTO(
                messageNot,
                ENotificationType.NOUVELLE_RESERVATION,
                saved.getId()
        );

        notificationService.envoyerNotification(bien.getProprietaire(), notification);


        return reservationMapper.toDTO(saved);
    }

    @Override
    public List<IndisponibiliteDTO> getIndisponibilitesParBien(Long bienId) {
        List<Reservation> reservations = reservationRepository.findByBienImmobilierIdAndStatut(bienId, EStatutReservation.CONFIRMEE);
        return reservations.stream()
                .map(res -> new IndisponibiliteDTO(res.getDateDebut(), res.getDateFin()))
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO confirmerReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(EStatutReservation.CONFIRMEE);
        reservation.setTotalPrice(reservation.getBienImmobilier().getPrix());
        reservation.setConfirmeParProprietaire(true);
        reservationRepository.save(reservation);

        // Notifier le client
        String message = String.format(
                "Votre réservation pour %s a été confirmée",
                reservation.getBienImmobilier().getTitre()
        );
        NotificationDTO notification = new NotificationDTO(
                message,
                ENotificationType.RESERVATION_CONFIRMEE,
                reservation.getId()
        );
        notificationService.envoyerNotification(reservation.getUtilisateur(), notification);

        // Annuler uniquement les réservations en conflit (même bien, statut EN_ATTENTE, dates qui se chevauchent)
        List<Reservation> concurrentes = reservationRepository
                .findByBienImmobilierIdAndStatutAndIdNot(
                        reservation.getBienImmobilier().getId(),
                        EStatutReservation.EN_ATTENTE,
                        reservation.getId()
                );

        for (Reservation r : concurrentes) {
            boolean chevauche = reservation.getDateDebut().isBefore(r.getDateFin()) &&
                    reservation.getDateFin().isAfter(r.getDateDebut());

            if (chevauche) {
                r.setStatut(EStatutReservation.ANNULEE);
            }
        }

        reservationRepository.saveAll(concurrentes);

        return reservationMapper.toDTO(reservation);
    }


    @Override
    public void annulerReservationParProp(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        reservation.setAnnuleParClient(true);
        reservation.setStatut(EStatutReservation.ANNULEE);
        String message = String.format(
                "Réservation #%d annulée par %s %s",
                reservation.getId(),
                reservation.getUtilisateur().getPrenom(),
                reservation.getUtilisateur().getNom()
        );

        NotificationDTO notification = new NotificationDTO(
                message,
                ENotificationType.RESERVATION_ANNULEE,
                reservation.getId()
        );

        notificationService.envoyerNotification(reservation.getPropietaire(), notification);
        reservationRepository.save(reservation);

    }
    @Override
    public void annulerReservationParClient(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setAnnuleParClient(true);
        reservation.setStatut(EStatutReservation.ANNULEE);

        // 🔔 Notification au propriétaire
        String notifMessage = String.format(
                "La réservation du bien '%s' a été annulée par le client %s %s",
                reservation.getBienImmobilier().getTitre(),
                reservation.getUtilisateur().getPrenom(),
                reservation.getUtilisateur().getNom()
        );

        NotificationDTO notification = new NotificationDTO(
                notifMessage,
                ENotificationType.RESERVATION_ANNULEE,
                reservation.getId()
        );

        notificationService.envoyerNotification(reservation.getPropietaire(), notification);

        // 📩 Email au propriétaire
        String emailSujet = "Réservation annulée par le client";
        String emailMessage = "Bonjour " + reservation.getPropietaire().getNom() + ",<br><br>" +
                "Le client <strong>" + reservation.getUtilisateur().getPrenom() + " " + reservation.getUtilisateur().getNom() + "</strong> " +
                "a annulé la réservation du bien situé à : <strong>" + reservation.getBienImmobilier().getAdresse() + "</strong>.<br><br>" +
                "Période initiale : du <strong>" + reservation.getDateDebut() + "</strong> au <strong>" + reservation.getDateFin() + "</strong><br><br>" +
                "Merci de vérifier les nouvelles disponibilités dans votre espace propriétaire.<br><br>" +
                "Cordialement,<br>L'équipe de Gestion Immobilière";

        emailService.envoyerEmail(reservation.getPropietaire().getEmail(), emailSujet, emailMessage);

        // 💾 Sauvegarde
        reservationRepository.save(reservation);
    }

    @Override
    public List<ReservationDTO> getReservationsParUtilisateur() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return reservationRepository.findByUtilisateurId(utilisateur.getId()).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReservationDTO> getReservationsParProprietaire() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return reservationRepository.findByPropietaireId(utilisateur.getId()).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReservationDTO> getReservationsParBien(Long bienId) {
        return reservationRepository.findByBienImmobilierId(bienId).stream()
                .map(reservationMapper::toDTO)
                .toList();
    }
}
