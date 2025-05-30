package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.Role;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String email;
    private String nom;
    private String prenom;
    private List<Role> roles;
}
