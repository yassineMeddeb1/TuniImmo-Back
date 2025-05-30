package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingReservationDTO {
    private Long id;
    private String bienTitle;
    private String clientName;
    private String dateDebut;
    private String dateFin;
    private int daysLeft;
}
