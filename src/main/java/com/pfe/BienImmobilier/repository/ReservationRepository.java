package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Abonnement;
import com.pfe.BienImmobilier.entities.EStatutReservation;
import com.pfe.BienImmobilier.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.bienImmobilier.proprietaire.id = :proprietaireId " +
            "AND r.confirmeParProprietaire = true " +
            "AND r.dateFin BETWEEN CURRENT_TIMESTAMP AND :threeDaysLater")
    List<Reservation> findReservationsEndingInNext3Days(
            @Param("proprietaireId") Long proprietaireId,
            @Param("threeDaysLater") LocalDateTime threeDaysLater);


    List<Reservation> findByStatutAndDateFinBefore(EStatutReservation statut, LocalDate date);

    @Query("SELECT b.id FROM BienImmobilier b WHERE NOT EXISTS (" +
            "SELECT r FROM Reservation r WHERE r.bienImmobilier.id = b.id " +
            "AND r.statut = 'CONFIRMEE' AND r.dateFin >= :aujourdhui)")
    List<Long> findBiensSansReservationsActives(@Param("aujourdhui") LocalDate aujourdHui);
    List<Reservation> findByUtilisateurId(Long utilisateurId);
    List<Reservation> findByBienImmobilierId(Long bienId);
    List<Reservation> findByPropietaireId(Long utilisateurId);
    List<Reservation> findByBienImmobilierIdAndStatut(Long bienId, EStatutReservation statut);

    @Query("SELECT r FROM Reservation r WHERE r.bienImmobilier.id = :bienId AND r.statut = 'CONFIRMEE'")
    List<Reservation> findIndisponibilitesByBien(@Param("bienId") Long bienId);
    List<Reservation> findByBienImmobilierIdAndStatutAndIdNot(Long bienId, EStatutReservation statut, Long id);
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE MONTH(r.dateReservation) = :month AND YEAR(r.dateReservation) = :year")
    double sumRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Reservation r WHERE YEAR(r.dateReservation) = :year")
    double sumRevenueByYear(@Param("year") int year);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.statut = :statut")
    long countByStatut(@Param("statut") EStatutReservation  statut);
}


