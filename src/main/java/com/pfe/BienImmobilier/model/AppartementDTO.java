package com.pfe.BienImmobilier.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppartementDTO {

    private String titre;
    private String description;
    private String adresse;
    private double prix;
    private boolean disponible;

    private Integer nombresChambres;
    private Integer nombresPièces;
    private Integer nombresSalledebain;
    private boolean jardin;
    private boolean garage;
    private boolean climatiseur;
    private boolean piscine;
    private boolean vueSurMer;

}

