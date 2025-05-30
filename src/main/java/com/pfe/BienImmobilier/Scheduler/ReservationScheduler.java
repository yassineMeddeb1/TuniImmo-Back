package com.pfe.BienImmobilier.Scheduler;

import com.pfe.BienImmobilier.entities.ENotificationType;
import com.pfe.BienImmobilier.entities.EStatutReservation;
import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.model.NotificationDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.ReservationRepository;
import com.pfe.BienImmobilier.services.inter.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final BienImmobilierRepository bienImmobilierRepository;

    // Chaque jour à minuit
    @Scheduled(cron = "0 0 0 * * ?")
    public void traiterReservationsTerminees() {
        LocalDate aujourdHui = LocalDate.now();

        // Trouver les réservations confirmées dont la date de fin est passée
        List<Reservation> reservations = reservationRepository
                .findByStatutAndDateFinBefore(EStatutReservation.CONFIRMEE, aujourdHui);

        for (Reservation reservation : reservations) {
            reservation.setStatut(EStatutReservation.TERMINEE);
            reservationRepository.save(reservation);

            // Notification au propriétaire
            String message = String.format(
                    "La réservation de %s est terminée.",
                    reservation.getBienImmobilier().getTitre()
            );

            NotificationDTO notification = new NotificationDTO(
                    message,
                    ENotificationType.RESERVATION_TERMINEE,
                    reservation.getId()
            );

            notificationService.envoyerNotification(reservation.getBienImmobilier().getProprietaire(), notification);
        }

        // (Optionnel) Libérer les biens si aucune autre réservation confirmée en cours
        libererBiensSiAucuneReservationActive();
    }

    private void libererBiensSiAucuneReservationActive() {
        List<Long> idsBiens = reservationRepository.findBiensSansReservationsActives(LocalDate.now());

        for (Long bienId : idsBiens) {
            // Rendre le bien disponible
            bienImmobilierRepository.mettreBienDisponible(bienId);
        }
    }
}
