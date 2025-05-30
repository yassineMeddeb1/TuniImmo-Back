//package com.pfe.BienImmobilier.services.impl;
//
//import com.pfe.BienImmobilier.entities.*;
//import com.pfe.BienImmobilier.exceptions.*;
//import com.pfe.BienImmobilier.model.*;
//import com.pfe.BienImmobilier.repository.*;
//import com.pfe.BienImmobilier.services.inter.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class AbonnementServiceImpl implements AbonnementService {
//
//    private final AbonnementRepository abonnementRepository;
//    private final UserRepository userRepository;
//    private final PaymeeService paymeeService;
//
//    @Override
//    @Transactional(readOnly = true)
//    public AbonnementDTO getCurrentAbonnement(Long userId) {
//        return abonnementRepository.findByUtilisateurIdAndStatus(userId, AbonnementStatus.ACTIVE)
//                .map(this::convertToDto)
//                .orElseThrow(() -> new NotFoundException("No active subscription found"));
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<AbonnementDTO> getAvailableAbonnements() {
//        return Arrays.stream(AbonnementType.values())
//                .map(this::createAbonnementTemplate)
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public PaymentResponseDTO initiatePayment(PaymentRequestDTO paymentRequest, Long userId) throws PaymeeException {
//        Utilisateur user = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("User not found"));
//
//        // Expire any active subscription
//        abonnementRepository.findByUtilisateurIdAndStatus(userId, AbonnementStatus.ACTIVE)
//                .ifPresent(abonnement -> {
//                    abonnement.setStatus(AbonnementStatus.EXPIRED);
//                    abonnementRepository.save(abonnement);
//                });
//
//        // Create new subscription
//        Abonnement newAbonnement = createNewAbonnement(paymentRequest, user);
//        abonnementRepository.save(newAbonnement);
//
//        // Initiate payment
//        PaymeePaymentRequest paymeeRequest = buildPaymeeRequest(paymentRequest, user, newAbonnement);
//        PaymeePaymentResponse response = paymeeService.createPayment(paymeeRequest);
//
//        // Update subscription with payment info
//        updateAbonnementWithPaymentInfo(newAbonnement, response);
//        abonnementRepository.save(newAbonnement);
//
//        return new PaymentResponseDTO(
//                response.getPaymentUrl(),
//                response.getToken(),
//                newAbonnement.getId()
//        );
//    }
//
//    @Override
//    @Transactional
//    public void handleWebhookNotification(PaymeeNotificationDTO notification) {
//        if (!paymeeService.verifyWebhookSignature(notification)) {
//            throw new SecurityException("Invalid webhook signature");
//        }
//
//        Abonnement abonnement = abonnementRepository.findByPaymentToken(notification.getToken())
//                .orElseThrow(() -> new NotFoundException("Subscription not found"));
//
//        if (Boolean.TRUE.equals(notification.getPaymentStatus())) {
//            handleSuccessfulPayment(abonnement, notification);
//        } else {
//            handleFailedPayment(abonnement);
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentStatusDTO getPaymentStatusByOrderId(String orderId) {
//        Abonnement abonnement = abonnementRepository.findByOrderId(orderId)
//                .orElseThrow(() -> new NotFoundException("Subscription not found for orderId: " + orderId));
//
//        return mapToPaymentStatus(abonnement);
//    }
//
//    private Abonnement createNewAbonnement(PaymentRequestDTO paymentRequest, Utilisateur user) {
//        AbonnementType type = AbonnementType.valueOf(paymentRequest.getType());
//
//        return Abonnement.builder()
//                .type(type)
//                .prix(calculatePrice(type))
//                .status(AbonnementStatus.PENDING)
//                .utilisateur(user)
//                .annoncesRestantes(calculateAnnonces(type))
//                .build();
//    }
//
//    private PaymeePaymentRequest buildPaymeeRequest(PaymentRequestDTO request, Utilisateur user, Abonnement abonnement) {
//        return PaymeePaymentRequest.builder()
//                .amount(request.getAmount())
//                .note(request.getNote())
//                .first_name(user.getPrenom())
//                .last_name(user.getNom())
//                .email(user.getEmail())
//                .phone("+216"+user.getTelephone())
//
//                .build();
//    }
//
//    private void updateAbonnementWithPaymentInfo(Abonnement abonnement, PaymeePaymentResponse response) {
//        abonnement.setPaymentToken(response.getToken());
//        abonnement.setOrderId(response.getOrderId());
//    }
//
//    private void handleSuccessfulPayment(Abonnement abonnement, PaymeeNotificationDTO notification) {
//        abonnement.setStatus(AbonnementStatus.ACTIVE);
//        abonnement.setDateDebut(LocalDateTime.now());
//        abonnement.setDateFin(calculateExpirationDate(abonnement.getType()));
//        abonnement.setTransactionId(notification.getTransactionId().toString());
//        abonnementRepository.save(abonnement);
//    }
//
//    private void handleFailedPayment(Abonnement abonnement) {
//        abonnement.setStatus(AbonnementStatus.FAILED);
//        abonnementRepository.save(abonnement);
//    }
//
//    private Abonnement createAbonnementTemplate(AbonnementType type) {
//        return Abonnement.builder()
//                .type(type)
//                .prix(calculatePrice(type))
//                .annoncesRestantes(calculateAnnonces(type))
//                .build();
//    }
//
//    private AbonnementDTO convertToDto(Abonnement abonnement) {
//        return AbonnementDTO.builder()
//                .id(abonnement.getId())
//                .type(abonnement.getType().name())
//                .prix(abonnement.getPrix())
//                .dateDebut(abonnement.getDateDebut())
//                .dateFin(abonnement.getDateFin())
//                .annoncesRestantes(abonnement.getAnnoncesRestantes())
//                .paymentToken(abonnement.getPaymentToken())
//                .orderId(abonnement.getOrderId())
//                .build();
//    }
//
//    private PaymentStatusDTO mapToPaymentStatus(Abonnement abonnement) {
//        return PaymentStatusDTO.builder()
//                .paymentToken(abonnement.getPaymentToken())
//                .orderId(abonnement.getOrderId())
//                .transactionId(abonnement.getTransactionId())
//                .amount(abonnement.getPrix())
//                .paymentDate(abonnement.getDateDebut())
//                .status(mapStatus(abonnement.getStatus()))
//                .statusMessage(getStatusMessage(abonnement.getStatus()))
//                .build();
//    }
//
//
//
//    // Helper methods for pricing and features
//    private double calculatePrice(AbonnementType type) {
//        switch (type) {
//            case GRATUIT: return 0.0;
//            case STANDARD: return 29.99;
//            case PREMIUM: return 59.99;
//            default: throw new IllegalArgumentException("Unknown subscription type");
//        }
//    }
//
//    private Integer calculateAnnonces(AbonnementType type) {
//        switch (type) {
//            case GRATUIT: return 3;
//            case STANDARD: return 10;
//            case PREMIUM: return 25;
//            default: return 0;
//        }
//    }
//
//    private LocalDateTime calculateExpirationDate(AbonnementType type) {
//        switch (type) {
//            case GRATUIT: return LocalDateTime.now().plusMonths(1);
//            case STANDARD: return LocalDateTime.now().plusMonths(3);
//            case PREMIUM: return LocalDateTime.now().plusMonths(6);
//            default: throw new IllegalArgumentException("Unknown subscription type");
//        }
//    }
//
//    private PaymentStatusDTO.PaymentStatus mapStatus(AbonnementStatus status) {
//        switch (status) {
//            case ACTIVE: return PaymentStatusDTO.PaymentStatus.COMPLETED;
//            case PENDING: return PaymentStatusDTO.PaymentStatus.PENDING;
//            case FAILED: return PaymentStatusDTO.PaymentStatus.FAILED;
//            case CANCELLED: return PaymentStatusDTO.PaymentStatus.CANCELLED;
//            case EXPIRED: return PaymentStatusDTO.PaymentStatus.EXPIRED;
//            default: return PaymentStatusDTO.PaymentStatus.PENDING;
//        }
//    }
//
//    private String getStatusMessage(AbonnementStatus status) {
//        switch (status) {
//            case ACTIVE: return "Payment completed successfully";
//            case PENDING: return "Payment pending";
//            case FAILED: return "Payment failed";
//            case CANCELLED: return "Payment cancelled";
//            case EXPIRED: return "Subscription expired";
//            default: return "Unknown status";
//        }
//    }
//}