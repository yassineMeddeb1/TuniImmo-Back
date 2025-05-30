package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.model.AvisDTO;
import com.pfe.BienImmobilier.entities.Avis;
import org.springframework.stereotype.Component;

@Component
public class AvisMapper {

    public AvisDTO toDto(Avis avis) {
        AvisDTO dto = new AvisDTO();
        dto.setId(avis.getId());
        dto.setNote(avis.getNote());
        dto.setCommentaire(avis.getCommentaire());
        dto.setDate(avis.getDate());
        dto.setClientId(avis.getAuteur().getId());
        dto.setClientNom(avis.getAuteur().getNom());
        dto.setClientPrenom(avis.getAuteur().getPrenom());
        dto.setBienImmobilierId(avis.getBienImmobilier().getId());
        return dto;
    }

    public Avis toEntity(AvisDTO dto) {
        Avis avis = new Avis();
        avis.setId(dto.getId());
        avis.setNote(dto.getNote());
        avis.setCommentaire(dto.getCommentaire());
        avis.setDate(dto.getDate());
        // Note: Les relations client et bien immobilier doivent être gérées séparément
        return avis;
    }
}