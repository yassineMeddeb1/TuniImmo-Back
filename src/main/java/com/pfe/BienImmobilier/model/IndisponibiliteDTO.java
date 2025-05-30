package com.pfe.BienImmobilier.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndisponibiliteDTO {
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
