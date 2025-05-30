package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean lu = false;

    private LocalDateTime dateEnvoi;
    private String type;
    private Long reservationId;
    @ManyToOne
    @JsonIgnore
    private Utilisateur destinataire;
}
