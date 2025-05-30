package com.pfe.BienImmobilier.model;

import lombok.Data;

import java.util.Date;

@Data
public class AvisDTO {
    private Long id;
    private int note;
    private String commentaire;
    private Date date;
    private Long clientId;
    private String clientNom;
    private String clientPrenom;
    private Long bienImmobilierId;
}