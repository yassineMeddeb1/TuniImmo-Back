package com.pfe.BienImmobilier.repository;



import com.pfe.BienImmobilier.entities.RoleType;
import com.pfe.BienImmobilier.entities.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    @EntityGraph(attributePaths = {"roles", "image"})
    Page<Utilisateur> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"roles", "image"})
    @Query("SELECT u FROM Utilisateur u " +
            "WHERE (:query IS NULL OR " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (" +
            ":role IS NULL OR " +
            "(:role = 'VISITEUR' AND SIZE(u.roles) = 1 AND EXISTS (SELECT r FROM u.roles r WHERE r.roleType = com.pfe.BienImmobilier.entities.RoleType.VISITEUR)) OR " +
            "(:role <> 'VISITEUR' AND EXISTS (SELECT r FROM u.roles r WHERE LOWER(r.roleType) = LOWER(:role)))" +
            ")")
    List<Utilisateur> findBySearchCriteria(@Param("query") String query,
                                           @Param("role") String role);


    @EntityGraph(attributePaths = {"reservations"})
    @Query("SELECT u FROM Utilisateur u WHERE u.id = :userId")
    Optional<Utilisateur> findByIdWithReservations(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"biensImmobiliers"})
    @Query("SELECT u FROM Utilisateur u WHERE u.id = :userId")
    Optional<Utilisateur> findByIdWithBiensImmobiliers(@Param("userId") Long userId);

    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(*) FROM Utilisateur" )
    long countByCreatedAtAfter();

    @Query("SELECT COUNT(DISTINCT r.utilisateur) FROM Reservation r WHERE r.dateReservation >= :date")
    long countActiveUsers(@Param("date") LocalDateTime date);
    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.roleType = :roleType")
    List<Utilisateur> findByRoleType(@Param("roleType") RoleType roleType);

}
