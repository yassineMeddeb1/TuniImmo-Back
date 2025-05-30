package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.exceptions.*;
import com.pfe.BienImmobilier.model.*;
import com.pfe.BienImmobilier.repository.*;
import com.pfe.BienImmobilier.services.inter.AbonnementService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    @Transactional(readOnly = true)
    public AbonnementDTO getCurrentAbonnement(Long userId) {
        return abonnementRepository.findByUtilisateurIdAndStatus(userId, AbonnementStatus.ACTIVE)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("No active subscription found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementDTO> getAvailableAbonnements() {
        return Arrays.stream(AbonnementType.values())
                .map(this::createAbonnementTemplate)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO paymentRequest, Long userId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Désactiver l'abonnement actif s'il existe
        abonnementRepository.findByUtilisateurIdAndStatus(userId, AbonnementStatus.ACTIVE)
                .ifPresent(abonnement -> {
                    abonnement.setStatus(AbonnementStatus.EXPIRED);
                    abonnementRepository.save(abonnement);
                });

        // Créer un nouvel abonnement
        Abonnement newAbonnement = createNewAbonnement(paymentRequest, user);
        abonnementRepository.save(newAbonnement);

        try {
            // Créer la session Stripe Checkout
            SessionCreateParams params = SessionCreateParams.builder()
                    .setCustomerEmail(user.getEmail())
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:4300/proprietaire/confirmation-paiement?success=true&session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:4300/proprietaire/paiement?canceled=true")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(getStripePriceId(paymentRequest.getType()))
                                    .setQuantity(1L)
                                    .build()
                    )
                    .putMetadata("abonnement_id", newAbonnement.getId().toString())
                    .putMetadata("user_id", userId.toString())
                    .build();

            Session session = Session.create(params);

            // Mettre à jour l'abonnement avec les infos de paiement
            newAbonnement.setPaymentToken(session.getId());
            abonnementRepository.save(newAbonnement);

            return new PaymentResponseDTO(session.getUrl(), session.getId(), newAbonnement.getId());

        } catch (StripeException e) {
            throw new PaymentException("Erreur lors de la création de la session Stripe: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public void handleWebhookNotification(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                handleCheckoutSessionCompleted(event);
            }
        } catch (Exception e) {
            throw new PaymentException("Signature webhook invalide");
        }
    }

    private void handleCheckoutSessionCompleted(Event event) throws StripeException {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
        Long abonnementId = Long.parseLong(session.getMetadata().get("abonnement_id"));

        Abonnement abonnement = abonnementRepository.findById(abonnementId)
                .orElseThrow(() -> new NotFoundException("Abonnement non trouvé"));

        abonnement.setStatus(AbonnementStatus.ACTIVE);
        abonnement.setDateDebut(LocalDateTime.now());
        abonnement.setDateFin(calculateExpirationDate(abonnement.getType()));
        abonnement.setTransactionId(session.getPaymentIntent());
        abonnementRepository.save(abonnement);
    }

    private String getStripePriceId(String abonnementType) {
        switch (AbonnementType.valueOf(abonnementType)) {
            case GRATUIT: return "price_1RIMmWQOBevw5P2MWBKbobe0";
            case STANDARD: return "price_1RIMqIQOBevw5P2MEzArxYNP";
            case PREMIUM: return "price_1RIMqyQOBevw5P2MdtlsketL";
            default: throw new IllegalArgumentException("Type d'abonnement inconnu");
        }
    }

    private Abonnement createNewAbonnement(PaymentRequestDTO request, Utilisateur user) {
        AbonnementType type = AbonnementType.valueOf(request.getType());

        return Abonnement.builder()
                .type(type)
                .prix(request.getAmount())
                .annoncesRestantes(calculateAnnonces(type))
                .dateDebut(LocalDateTime.now())
                .dateFin(calculateExpirationDate(type))
                .status(AbonnementStatus.ACTIVE)
                .utilisateur(user)
                .annoncesRestantes(calculateAnnonces(type))
                .build();
    }

    private AbonnementDTO convertToDto(Abonnement abonnement) {
        return AbonnementDTO.builder()
                .id(abonnement.getId())
                .type(abonnement.getType().name())
                .prix(abonnement.getPrix())
                .dateDebut(abonnement.getDateDebut())
                .dateFin(abonnement.getDateFin())
                .annoncesRestantes(abonnement.getAnnoncesRestantes())
                .status(abonnement.getStatus().name())
                .paymentToken(abonnement.getPaymentToken())
                .build();
    }

    private Integer calculateAnnonces(AbonnementType type) {
        switch (type) {
            case GRATUIT: return 3;
            case STANDARD: return 10;
            case PREMIUM: return 25;
            default: return 0;
        }
    }

    private LocalDateTime calculateExpirationDate(AbonnementType type) {
        switch (type) {
            case GRATUIT: return LocalDateTime.now().plusMonths(1);
            case STANDARD: return LocalDateTime.now().plusMonths(3);
            case PREMIUM: return LocalDateTime.now().plusMonths(6);
            default: throw new IllegalArgumentException("Type d'abonnement inconnu");
        }
    }
    private Abonnement createAbonnementTemplate(AbonnementType type) {
        return Abonnement.builder()
                .type(type)
                .prix(getPrixParType(type))
                .annoncesRestantes(calculateAnnonces(type))
                .dateDebut(LocalDateTime.now())
                .dateFin(calculateExpirationDate(type))
                .status(AbonnementStatus.PENDING)
                .build();
    }
    private Double getPrixParType(AbonnementType type) {
        switch (type) {
            case GRATUIT: return 0.0;
            case STANDARD: return 29.99; // exemple
            case PREMIUM: return 59.99;  // exemple
            default: throw new IllegalArgumentException("Type d'abonnement inconnu");
        }
    }

}