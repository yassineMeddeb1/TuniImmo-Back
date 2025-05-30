package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Categorie;
import com.pfe.BienImmobilier.entities.Commune;
import com.pfe.BienImmobilier.repository.CommuneRepository;
import com.pfe.BienImmobilier.services.impl.CommuneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/communes")
@RequiredArgsConstructor
public class CommuneController {

    private final CommuneService communeService;
    private final CommuneRepository communeRepository;

    @GetMapping("/byGouvernorat/{gouvernoratId}")
    public ResponseEntity<List<Commune>> getCommunesByGouvernorat(@PathVariable Long gouvernoratId) {
        return ResponseEntity.ok(communeService.getCommunesByGouvernorat(gouvernoratId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Commune> getcommuenById(@PathVariable Long id) {
        Optional<Commune> communeOptional = communeRepository.findById(id);

        if (communeOptional.isPresent()) {
            return new ResponseEntity<>(communeOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Retourne 404 si la catégorie n'est pas trouvée
        }
    }
}
