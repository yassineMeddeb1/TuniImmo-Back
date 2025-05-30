package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;

import java.util.List;

public interface FavorisService {
    List<BienImmobilierDTO> ajouterFavori(Long utilisateurId, Long bienId);
    List<BienImmobilierDTO> supprimerFavori(Long utilisateurId, Long bienId);
    List<BienImmobilierDTO> obtenirFavoris(Long utilisateurId);
    }
