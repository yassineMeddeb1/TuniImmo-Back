package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Abonnement;
import com.pfe.BienImmobilier.entities.AbonnementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByUtilisateurId(Long userId);
    Optional<Abonnement> findByUtilisateurIdAndStatus(Long utilisateurId, AbonnementStatus status);
    Optional<Abonnement> findByPaymentToken(String paymentToken);
    List<Abonnement> findByUtilisateurIdOrderByDateFinDesc(Long utilisateurId);

}