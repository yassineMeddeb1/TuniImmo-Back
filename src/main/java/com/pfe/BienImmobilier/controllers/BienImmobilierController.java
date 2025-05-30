package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.model.BienImmobilierFilterDTO;
import com.pfe.BienImmobilier.services.impl.BienImmobilierServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BienImmobilierController {

    private final BienImmobilierServiceImpl bienImmobilierService;
    @PutMapping("/biens/user/{bienId}/view")
    public ResponseEntity<Void> incrementView(@PathVariable Long bienId) {
        bienImmobilierService.incrementerViews(bienId); // Incrémenter le nombre de vues
        return ResponseEntity.ok().build(); // Retourner une réponse vide
    }
    @PostMapping("/biens/user/search")
    public ResponseEntity<Page<BienImmobilierDTO>> searchBiens(
            @RequestBody BienImmobilierFilterDTO filter, Pageable pageable) {
        return ResponseEntity.ok(bienImmobilierService.searchBiens(filter, pageable));
    }

    @GetMapping("/biens/user/top-offers")
    public ResponseEntity<List<BienImmobilierDTO>> getTopOffers() {
        return ResponseEntity.ok(bienImmobilierService.getTopOffers());
    }

    @GetMapping("/biens/user/today-added")
    public ResponseEntity<List<BienImmobilierDTO>> getTodayAdded() {
        return ResponseEntity.ok(bienImmobilierService.getTodayAdded());
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<BienImmobilierDTO>> getByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(bienImmobilierService.getByCategorie(categorie));
    }

    @GetMapping("/biens/user/{id}")
    public ResponseEntity<BienImmobilierDTO> getBienById(@PathVariable Long id) {
        return ResponseEntity.ok(bienImmobilierService.getBienById(id));
    }


    @PostMapping("/proprietaire")
    public ResponseEntity<BienImmobilierDTO> createBien(@RequestBody BienImmobilier bien) {
        return ResponseEntity.ok(bienImmobilierService.createBien(bien));
    }

    @PutMapping("/proprietaire/{id}")
    public ResponseEntity<BienImmobilierDTO> updateBien(@PathVariable Long id, @RequestBody BienImmobilier bienDetails) {
        return ResponseEntity.ok(bienImmobilierService.updateBien(id, bienDetails));
    }
    @GetMapping("/proprietaire/mes-biens")
    public ResponseEntity<List<BienImmobilierDTO>> getBiensByProprietaire() {
        return ResponseEntity.ok(bienImmobilierService.getBiensDuProprietaireConnecte());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBien(@PathVariable Long id) {
        bienImmobilierService.deleteBien(id);
        return ResponseEntity.noContent().build();
    }
}