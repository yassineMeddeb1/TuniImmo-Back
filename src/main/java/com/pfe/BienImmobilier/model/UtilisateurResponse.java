package com.pfe.BienImmobilier.model;

import lombok.Data;

@Data
public class UtilisateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String adresse;
    private String region;
    private String telephone;
    private String email;
}
