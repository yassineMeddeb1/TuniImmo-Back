package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class UserReservationsDTO {
    private Long userId;
    private List<Reservation> reservations;
}