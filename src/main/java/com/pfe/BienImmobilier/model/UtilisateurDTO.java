package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private List<Role> roles;


}

