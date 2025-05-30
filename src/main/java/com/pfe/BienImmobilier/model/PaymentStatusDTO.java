package com.pfe.BienImmobilier.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentStatusDTO {
    private String paymentToken;
    private String orderId;
    private String transactionId;
    private Double amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String statusMessage;

    public enum PaymentStatus {
        PENDING, COMPLETED,EXPIRED, FAILED, CANCELLED, REFUNDED
    }
}