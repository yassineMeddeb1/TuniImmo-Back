package com.pfe.BienImmobilier.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AbonnementDTO {
    private Long id;
    private String type;
    private Double prix;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Integer annoncesRestantes;
    private String status;
    private String paymentToken;
    private String orderId;
}