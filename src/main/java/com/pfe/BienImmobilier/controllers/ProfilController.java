package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.UpdateProfileRequest;
import com.pfe.BienImmobilier.model.UtilisateurRequest;
import com.pfe.BienImmobilier.model.UtilisateurResponse;
import com.pfe.BienImmobilier.services.impl.ProfilService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profil")
public class ProfilController {

    @Autowired
    private ProfilService profilService;

    @GetMapping
    public ResponseEntity<UtilisateurResponse> getProfil(HttpServletRequest request) {
        return ResponseEntity.ok(profilService.getUtilisateurDepuisToken(request));
    }

    @PutMapping("/modifier")
    public ResponseEntity<UtilisateurResponse> updateProfile(@RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(profilService.modifierProfil(request, httpRequest));
    }

}
