package com.pfe.BienImmobilier.services.inter;

import com.pfe.BienImmobilier.exceptions.PaymentException;
import com.pfe.BienImmobilier.model.*;

import java.util.List;

public interface AbonnementService {
    AbonnementDTO getCurrentAbonnement(Long userId);
    List<AbonnementDTO> getAvailableAbonnements();
    PaymentResponseDTO initiatePayment(PaymentRequestDTO paymentRequest, Long userId);
    void handleWebhookNotification(String payload, String sigHeader);
}