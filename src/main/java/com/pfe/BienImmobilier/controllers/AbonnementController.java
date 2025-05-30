package com.pfe.BienImmobilier.controllers;


import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.services.inter.AbonnementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {

    private final AbonnementService abonnementService;

    @GetMapping("/current")
    public ResponseEntity<AbonnementDTO> getCurrentAbonnement(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(abonnementService.getCurrentAbonnement(userId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AbonnementDTO>> getAvailableAbonnements() {
        return ResponseEntity.ok(abonnementService.getAvailableAbonnements());
    }

    @PostMapping("/initiate-payment")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(abonnementService.initiatePayment(paymentRequest, userId));
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        abonnementService.handleWebhookNotification(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}