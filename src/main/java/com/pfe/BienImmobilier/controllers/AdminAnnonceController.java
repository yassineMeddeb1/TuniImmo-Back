package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.services.impl.BienImmobilierServiceImpl;
import com.pfe.BienImmobilier.services.inter.BienImmobilierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/annonces")
@RequiredArgsConstructor
public class AdminAnnonceController {
    private final BienImmobilierServiceImpl bienService;

    @GetMapping
    public ResponseEntity<Page<BienImmobilierDTO>> getAnnoncesAdmin(
            @RequestParam(required = false) Integer statut,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String search,
            Pageable pageable) {

        return ResponseEntity.ok(
                bienService.getAnnoncesAdmin(statut, categorie, search, pageable)
        );
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Void> updateStatutAnnonce(
            @PathVariable Long id,
            @RequestParam Integer statut) {

        bienService.updateStatutAdmin(id, statut);
        return ResponseEntity.ok().build();
    }
}