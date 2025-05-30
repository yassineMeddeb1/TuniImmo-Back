package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.AvisDTO;
import com.pfe.BienImmobilier.model.AvisRequestDTO;
import com.pfe.BienImmobilier.services.inter.AvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService; // Ajout de final

    @PostMapping
    public ResponseEntity<AvisDTO> createAvis(
            @RequestBody AvisRequestDTO avisRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        AvisDTO createdAvis = avisService.createAvis(avisRequestDTO, userDetails.getUsername());
        return ResponseEntity.ok(createdAvis);
    }

    @GetMapping("/bien/{bienId}")
    public ResponseEntity<List<AvisDTO>> getAvisByBienId(@PathVariable Long bienId) {
        List<AvisDTO> avisList = avisService.getAvisByBienId(bienId);
        return ResponseEntity.ok(avisList);
    }

    @GetMapping("/bien/{bienId}/moyenne")
    public ResponseEntity<Double> getAverageRatingByBienId(@PathVariable Long bienId) {
        double moyenne = avisService.getAverageRatingByBienId(bienId);
        return ResponseEntity.ok(moyenne);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvis(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        avisService.deleteAvis(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}