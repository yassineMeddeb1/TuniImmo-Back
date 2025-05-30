package com.pfe.BienImmobilier.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AbonnementType type;

    private Double prix;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer annoncesRestantes;

    @Enumerated(EnumType.STRING)
    private AbonnementStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    private String paymentToken;
    private String transactionId;
}

