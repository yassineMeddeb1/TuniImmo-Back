package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.entities.BienImmobilier;

import java.util.List;

public interface BienImmobilierService {
    BienImmobilier createBien(BienImmobilierDTO bienImmobilierDTO);
//    List<BienImmobilier> listerBiens();
//    List<BienImmobilier> listerBiensParProprietaire(Long proprietaireId);
//    void supprimerBien(Long id);
}
