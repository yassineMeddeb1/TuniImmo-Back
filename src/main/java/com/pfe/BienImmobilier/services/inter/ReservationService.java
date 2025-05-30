package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.model.IndisponibiliteDTO;
import com.pfe.BienImmobilier.model.ReservationDTO;

import java.util.List;

public interface ReservationService {
    ReservationDTO creerReservation(Reservation reservation, Long bienId);
    ReservationDTO confirmerReservation(Long reservationId);
    void annulerReservationParClient(Long reservationId);
    List<ReservationDTO> getReservationsParUtilisateur();
    List<ReservationDTO> getReservationsParProprietaire();
    List<ReservationDTO> getReservationsParBien(Long bienId);
    public void annulerReservationParProp(Long reservationId);
    List<IndisponibiliteDTO> getIndisponibilitesParBien(Long bienId);
}
