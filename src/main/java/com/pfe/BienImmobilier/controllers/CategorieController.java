package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Categorie;
import com.pfe.BienImmobilier.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/categories")
public class CategorieController {

    private final CategorieRepository categorieRepository;

    @Autowired
    public CategorieController(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    // Récupérer toutes les catégories
    @GetMapping
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieRepository.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Récupérer une catégorie par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);

        if (categorieOptional.isPresent()) {
            return new ResponseEntity<>(categorieOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne 404 si la catégorie n'est pas trouvée
        }
    }


    // Créer une nouvelle catégorie
    @PostMapping
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie newCategorie = categorieRepository.save(categorie);
        return new ResponseEntity<>(newCategorie, HttpStatus.CREATED);
    }

    // Modifier une catégorie existante
    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorieDetails) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);

        if (categorieOptional.isPresent()) {
            Categorie categorie = categorieOptional.get();
            categorie.setNom(categorieDetails.getNom());
            Categorie updatedCategorie = categorieRepository.save(categorie);
            return new ResponseEntity<>(updatedCategorie, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne 404 si la catégorie n'est pas trouvée
        }
    }


    // Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        Optional<Categorie> categorieOptional = categorieRepository.findById(id);

        if (categorieOptional.isPresent()) {
            categorieRepository.delete(categorieOptional.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Retourne 204 si la suppression est réussie
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne 404 si la catégorie n'est pas trouvée
        }
    }

}
