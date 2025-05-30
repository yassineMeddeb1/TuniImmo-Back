package com.pfe.BienImmobilier.model;

import lombok.Getter;
import lombok.Setter;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDTO {
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private LocalDateTime dateReservation;
    private String statut;
    private double TotalPrice;
    private Long utilisateurId;
    private UtilisateurDTO client;
    private BienImmobilierDTO bien;
    private Long bienId;
    private Boolean confirmeParProprietaire;
    private Boolean annuleParClient;
    private String commentaire;
}
