package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<BienImmobilier, Long> {

    @Query("SELECT new com.pfe.BienImmobilier.model.MonthlyRevenueDTO(" +
            "MONTH(r.dateDebut), " +
            "YEAR(r.dateDebut), " +
            "SUM(r.totalPrice)) " +
            "FROM Reservation r " +
            "WHERE r.bienImmobilier.proprietaire.id = :proprietaireId " +
            "AND r.confirmeParProprietaire = true " +
            "AND CURRENT_DATE BETWEEN r.dateDebut AND r.dateFin " +
            "GROUP BY MONTH(r.dateDebut), YEAR(r.dateDebut) " +
            "ORDER BY YEAR(r.dateDebut), MONTH(r.dateDebut)")
    List<MonthlyRevenueDTO> findMonthlyRevenueForCurrentYear(
            @Param("proprietaireId") Long proprietaireId);
    @Query("SELECT new com.pfe.BienImmobilier.model.PropertyDistributionDTO(" +
            "c.nom, COUNT(b)) " +
            "FROM BienImmobilier b " +
            "JOIN b.categorie c " +
            "WHERE b.proprietaire.id = :proprietaireId " +
            "GROUP BY c.nom")
    List<PropertyDistributionDTO> findPropertyDistributionByProprietaire(@Param("proprietaireId") Long proprietaireId);

    @Query("SELECT new com.pfe.BienImmobilier.model.TopClientDTO(" +
            "u.id, CONCAT(u.prenom, ' ', u.nom), " +
            "CONCAT(SUBSTRING(u.prenom, 1, 1), SUBSTRING(u.nom, 1, 1)), " +
            "COUNT(r)) " +
            "FROM Reservation r " +
            "JOIN r.utilisateur u " +
            "WHERE r.bienImmobilier.proprietaire.id = :proprietaireId " +
            "AND r.confirmeParProprietaire = true " +
            "GROUP BY u.id, u.prenom, u.nom " +
            "ORDER BY COUNT(r) DESC")
    List<TopClientDTO> findTopClientsByProprietaire(@Param("proprietaireId") Long proprietaireId, Pageable pageable);

    @Query("SELECT new com.pfe.BienImmobilier.model.TopPropertyDTO(" +
            "b.id, b.titre, " +
            "b.views, " +
            "COUNT(r)) " +
            "FROM BienImmobilier b " +
            "LEFT JOIN Reservation r ON r.bienImmobilier.id = b.id " +
            "WHERE b.proprietaire.id = :proprietaireId " +
            "AND b.views IS NOT NULL " +
            "GROUP BY b.id, b.titre, b.views " +
            "ORDER BY COUNT(r) DESC, b.views DESC")
    List<TopPropertyDTO> findTopPropertiesByProprietaire(@Param("proprietaireId") Long proprietaireId, Pageable pageable);
    @Query("SELECT COUNT(b) FROM BienImmobilier b WHERE b.proprietaire.id = :proprietaireId")
    int countPropertiesByProprietaire(@Param("proprietaireId") Long proprietaireId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.bienImmobilier.proprietaire.id = :proprietaireId " +
            "AND r.confirmeParProprietaire = true " +
            "AND FUNCTION('MONTH', r.dateReservation) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', r.dateReservation) = FUNCTION('YEAR', CURRENT_DATE)")
    int countMonthlyReservationsByProprietaire(@Param("proprietaireId") Long proprietaireId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.bienImmobilier.proprietaire.id = :proprietaireId " +
            "AND r.confirmeParProprietaire = true " +
            "AND r.dateFin >= CURRENT_DATE")
    int countActiveReservationsByProprietaire(@Param("proprietaireId") Long proprietaireId);

    @Query(value = "SELECT AVG(DATEDIFF(r.date_fin, r.date_debut)) " +
            "FROM reservations r " +
            "JOIN biens_immobiliers b ON r.bien_id = b.id " +
            "WHERE b.proprietaire_id = :ownerId " +
            "AND r.confirme_par_proprietaire = true " +
            "AND r.date_fin >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)",
            nativeQuery = true)
    Double calculateAverageOccupancyRate(@Param("ownerId") Long ownerId);
}