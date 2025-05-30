package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.AdminUserDTO;
import com.pfe.BienImmobilier.model.UtilisateurDTO;
import com.pfe.BienImmobilier.model.UtilisateurResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {
    UtilisateurResponse toResponse(Utilisateur utilisateur);
    @Mapping(target = "role", source = "roles", qualifiedByName = "getMainRole")

    AdminUserDTO toDto(Utilisateur utilisateur);

    @Named("getMainRole")
    default String getMainRole(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "VISITEUR";
        }
        return roles.get(0).getRoleType().name();
    }
}
