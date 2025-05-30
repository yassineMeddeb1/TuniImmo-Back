package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.config.NotificationWebSocketHandler;
import com.pfe.BienImmobilier.entities.Notification;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.NotificationDTO;
import com.pfe.BienImmobilier.repository.NotificationRepository;
import com.pfe.BienImmobilier.services.inter.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketHandler webSocketHandler;

    @Override
    @Transactional
    public void envoyerNotification(Utilisateur destinataire, NotificationDTO notificationDTO) {
        // Sauvegarder en base
        Notification notif = Notification.builder()
                .destinataire(destinataire)
                .message(notificationDTO.getMessage())
                .type(notificationDTO.getType().name())
                .dateEnvoi(LocalDateTime.now())
                .lu(false)
                .reservationId(notificationDTO.getReservationId())
                .build();

        notificationRepository.save(notif);

        // Mettre à jour l'ID pour le DTO
        notificationDTO.setId(notif.getId());
        notificationDTO.setDate(notif.getDateEnvoi().toString());

        // Envoyer via WebSocket
        try {
            webSocketHandler.sendNotificationToUser(
                    destinataire.getId().toString(),
                    notificationDTO
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to send WebSocket notification", e);
        }
    }

    @Override
    public List<Notification> getNotificationsNonLues(Utilisateur utilisateur) {
        return notificationRepository.findByDestinataireAndLuFalse(utilisateur);
    }
    @Override
    public void marquerToutesCommeLues(List<Long> ids) {
        List<Notification> notifications = notificationRepository.findAllById(ids);
        for (Notification notif : notifications) {
            notif.setLu(true);
        }
        notificationRepository.saveAll(notifications);
    }


    @Override
    @Transactional
    public void marquerCommeLue(Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notif.setLu(true);
        notificationRepository.save(notif);
    }
}