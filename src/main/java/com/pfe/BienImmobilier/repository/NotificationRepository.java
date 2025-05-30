package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Notification;
import com.pfe.BienImmobilier.entities.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByDestinataire(Utilisateur destinataire, Pageable pageable);
    List<Notification> findByDestinataireAndLuFalse(Utilisateur destinataire);
}
