package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Categorie;
import com.pfe.BienImmobilier.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    @Autowired
    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    // Récupérer toutes les catégories
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    // Récupérer une catégorie par son nom
    public Categorie getCategorieByNom(String nom) {
        return categorieRepository.findByNom(nom);
    }

    // Ajouter une nouvelle catégorie
    public Categorie addCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    // Supprimer une catégorie
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
