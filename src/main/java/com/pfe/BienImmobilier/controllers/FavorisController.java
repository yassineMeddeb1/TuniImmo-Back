package com.pfe.BienImmobilier.controller;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.services.inter.FavorisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favoris")
@RequiredArgsConstructor
public class FavorisController {

    private final FavorisService favorisService;

    @PostMapping("/ajouter")
    public List<BienImmobilierDTO> ajouterFavori(@RequestParam Long utilisateurId, @RequestParam Long bienId) {
        return favorisService.ajouterFavori(utilisateurId, bienId);
    }

    @DeleteMapping("/supprimer")
    public List<BienImmobilierDTO> supprimerFavori(@RequestParam Long utilisateurId, @RequestParam Long bienId) {
        return favorisService.supprimerFavori(utilisateurId, bienId);
    }

    @GetMapping("/obtenir")
    public List<BienImmobilierDTO> obtenirFavoris(@RequestParam Long utilisateurId) {
        return favorisService.obtenirFavoris(utilisateurId);
    }
}
