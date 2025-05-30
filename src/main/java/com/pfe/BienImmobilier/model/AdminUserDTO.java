package com.pfe.BienImmobilier.model;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private boolean enabled;
    private String role; // Stocke directement le nom du rôle

}



