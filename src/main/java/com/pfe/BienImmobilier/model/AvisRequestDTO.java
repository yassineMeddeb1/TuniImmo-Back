package com.pfe.BienImmobilier.model;

import lombok.Data;

@Data
public class AvisRequestDTO {
    private int note;
    private String commentaire;
    private Long bienImmobilierId;
}