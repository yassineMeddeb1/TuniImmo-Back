package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateFin;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateReservation;
    private double totalPrice;

    // Statut de la réservation
    @Enumerated(EnumType.STRING)
    private EStatutReservation statut = EStatutReservation.EN_ATTENTE;

    // Utilisateur ayant fait la réservation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietaire_id")
    private Utilisateur propietaire;

    // Bien réservé
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_id")
    private BienImmobilier bienImmobilier;




    private Boolean confirmeParProprietaire = false;

    private Boolean annuleParClient = false;

    private String commentaire; // Pour refus ou autres remarques
}
