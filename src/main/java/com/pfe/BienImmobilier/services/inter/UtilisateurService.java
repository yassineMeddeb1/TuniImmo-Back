package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.entities.RoleType;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.AdminUserDTO;
import com.pfe.BienImmobilier.model.UserAnnoncesDTO;
import com.pfe.BienImmobilier.model.UserReservationsDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UtilisateurService {
    Page<AdminUserDTO> getUsersPaginated(int page, int size, String sortField);
    List<AdminUserDTO> searchUsers(String query, String role);
    void deleteUser(Long id);
    AdminUserDTO toggleUserStatus(Long id);
    UserReservationsDTO getUserReservations(Long userId);
    UserAnnoncesDTO getUserAnnonces(Long userId);
    }

