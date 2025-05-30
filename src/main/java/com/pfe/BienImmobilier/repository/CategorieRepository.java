package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    // Vous pouvez ajouter des méthodes spécifiques de recherche si nécessaire
    Categorie findByNom(String nom);
}
