package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Gouvernorat;
import com.pfe.BienImmobilier.services.GouvernoratService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4300")
@RestController
@RequestMapping("/api/gouvernorats")
@RequiredArgsConstructor
public class GouvernoratController {

    private final GouvernoratService gouvernoratService;

    @GetMapping
    public ResponseEntity<List<Gouvernorat>> getAllGouvernorats() {
        return ResponseEntity.ok(gouvernoratService.getAllGouvernorats());
    }

    @GetMapping("/{gouvernoratId}")
    public ResponseEntity<Gouvernorat> getGouvernoratById(@PathVariable Long gouvernoratId) {
        Optional<Gouvernorat> gouvernorat = gouvernoratService.getGouvernoratById(gouvernoratId);
        return gouvernorat.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
