package com.pfe.BienImmobilier.model;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nom;
    private String prenom;
    private String adresse;
    private String region;
    private String telephone;
    private String currentPassword;
    private String newPassword;
}
