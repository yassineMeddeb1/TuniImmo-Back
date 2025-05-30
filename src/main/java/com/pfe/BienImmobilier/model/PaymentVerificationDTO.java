package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentVerificationDTO {
    private Boolean success;
    private String message;
    private AbonnementDTO abonnement;
}