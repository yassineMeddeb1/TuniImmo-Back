package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurRequest {
    private String nom;
    private String prenom;
    private String adresse;
    private String region;
    private String email;
    private String motDePasse;
    private String telephone;
    private Set<RoleType> roles;
}
