package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.RoleType;
import lombok.Data;

import java.util.List;

@Data
public class SignupRequestDTO {
    private String nom;
    private String prenom;
    private String region;
    private String adresse;
    private String telephone;
    private String email;
    private String motDePasse;
    private String image;
    private List<RoleType> roles; // Liste des rôles attribués

}