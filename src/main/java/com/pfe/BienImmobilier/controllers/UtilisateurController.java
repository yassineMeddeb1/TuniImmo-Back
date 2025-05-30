package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.services.inter.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService userService;

    @GetMapping
    public Page<AdminUserDTO> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy) {
        return userService.getUsersPaginated(page, size, sortBy);
    }

    @GetMapping("/search")
    public List<AdminUserDTO> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role) {
        return userService.searchUsers(query, role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminUserDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<UserReservationsDTO> getReservations(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserReservations(id));
    }

    @GetMapping("/{id}/annonces")
    public ResponseEntity<UserAnnoncesDTO> getAnnonces(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserAnnonces(id));
    }
}