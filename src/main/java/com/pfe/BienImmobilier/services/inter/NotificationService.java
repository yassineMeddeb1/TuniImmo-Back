package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.entities.Notification;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.NotificationDTO;

import java.util.List;

public interface NotificationService {
    void envoyerNotification(Utilisateur destinataire, NotificationDTO notificationDTO);
    List<Notification> getNotificationsNonLues(Utilisateur utilisateur);
    void marquerCommeLue(Long id);
    void marquerToutesCommeLues(List<Long> ids);

}