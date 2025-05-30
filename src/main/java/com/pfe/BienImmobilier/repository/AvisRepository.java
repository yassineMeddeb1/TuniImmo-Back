package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Avis;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {
    List<Avis> findByBienImmobilierOrderByDateDesc(BienImmobilier bienImmobilier);

    boolean existsByAuteurAndBienImmobilier(Utilisateur auteur, BienImmobilier bienImmobilier);

    @Query("SELECT AVG(a.note) FROM Avis a WHERE a.bienImmobilier = :bienImmobilier")
    Optional<Double> getAverageRatingByBienImmobilier(BienImmobilier bienImmobilier);
}