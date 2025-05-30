package com.pfe.BienImmobilier.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerrainDTO {

    private String titre;
    private String description;
    private String adresse;
    private double prix;
    private boolean disponible;

    private double superficie;
    private boolean constructible;

    // Getters et Setters
}