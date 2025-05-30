package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.model.AvisDTO;
import com.pfe.BienImmobilier.model.AvisRequestDTO;

import java.util.List;

public interface AvisService {
    AvisDTO createAvis(AvisRequestDTO avisRequestDTO, String userEmail);
    List<AvisDTO> getAvisByBienId(Long bienId);
    double getAverageRatingByBienId(Long bienId);
    void deleteAvis(Long id, String userEmail);
}