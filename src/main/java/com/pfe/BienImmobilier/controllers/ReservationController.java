package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.model.IndisponibiliteDTO;
import com.pfe.BienImmobilier.model.ReservationDTO;
import com.pfe.BienImmobilier.services.inter.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{bienId}")
    public ResponseEntity<ReservationDTO> creerReservation(
            @RequestBody Reservation reservation,
            @PathVariable Long bienId
            ) {
        ReservationDTO dto = reservationService.creerReservation(reservation, bienId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/proprietaire/{id}/confirmer")
    public ResponseEntity<ReservationDTO> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmerReservation(id));
    }

    @PostMapping("/proprietaire/{id}/annuler")
    public ResponseEntity<Void> annuler(@PathVariable Long id) {
        reservationService.annulerReservationParProp(id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("user/annuler/{id}")
    public ResponseEntity<Void> annulerParClient(@PathVariable("id") Long reservationId) {
        reservationService.annulerReservationParClient(reservationId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/user/reservation")
    public ResponseEntity<List<ReservationDTO>> getByUser() {
        return ResponseEntity.ok(reservationService.getReservationsParUtilisateur());
    }

    @GetMapping("/proprietaire")
    public ResponseEntity<List<ReservationDTO>> getByProprietaire() {
        return ResponseEntity.ok(reservationService.getReservationsParProprietaire());
    }

    @GetMapping("/bien/{bienId}")
    public ResponseEntity<List<ReservationDTO>> getByBien(@PathVariable Long bienId) {
        return ResponseEntity.ok(reservationService.getReservationsParBien(bienId));
    }
    @GetMapping("/bien/{id}/indisponibilites")
    public ResponseEntity<List<IndisponibiliteDTO>> getIndisponibilites(@PathVariable Long id) {
        List<IndisponibiliteDTO> indisponibilites = reservationService.getIndisponibilitesParBien(id);
        return ResponseEntity.ok(indisponibilites);
    }

}
