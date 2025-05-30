
package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponseDTO {
    private String paymentUrl;
    private String paymentToken;
    private Long abonnementId;
}