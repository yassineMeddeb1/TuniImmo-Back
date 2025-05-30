//package com.pfe.BienImmobilier.services.impl;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pfe.BienImmobilier.config.PaymeeConfig;
//import com.pfe.BienImmobilier.exceptions.PaymeeException;
//import com.pfe.BienImmobilier.model.*;
//import com.pfe.BienImmobilier.services.inter.PaymeeService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.*;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PaymeeServiceImpl implements PaymeeService {
//
//    private final RestTemplate restTemplate;
//    private final PaymeeConfig paymeeConfig;
//
//    @Override
//    public PaymeePaymentResponse createPayment(PaymeePaymentRequest request) throws PaymeeException {
//        try {
//            // Set required URLs
//            request.setOrder_id(String.format("%06d", (int)(Math.random() * 1000000)));
//            request.setWebhook_url(paymeeConfig.getWebhookUrl());
//            request.setReturn_url(paymeeConfig.getRedirectUrl());
//            request.setCancel_url(paymeeConfig.getCancelUrl());
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Token " + paymeeConfig.getApiKey());
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<PaymeePaymentRequest> entity = new HttpEntity<>(request, headers);
//
//            ResponseEntity<PaymeePaymentResponse> response = restTemplate.exchange(
//                    paymeeConfig.getApiUrl() + "/api/v2/payments/create",
//                    HttpMethod.POST,
//                    entity,
//                    PaymeePaymentResponse.class
//            );
//
//            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
//                throw new PaymeeException("Failed to create payment");
//            }
//            if (response.getBody()== null) {
//                throw new RuntimeException("Erreur: La réponse Paymee ne contient pas de données.");
//            }
//            System.out.println("Réponse brute Paymee: " + response.getBody());
//            System.out.println(new ObjectMapper().writeValueAsString(request));
//            return response.getBody();
//
//        } catch (RestClientException e) {
//            log.error("Error creating Paymee payment", e);
//            throw new PaymeeException("Technical error while creating payment");
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public boolean verifyWebhookSignature(PaymeeNotificationDTO notification) {
//        String calculatedChecksum = DigestUtils.md5Hex(
//                notification.getToken() +
//                        (notification.getPaymentStatus() ? "1" : "0") +
//                        paymeeConfig.getApiKey()
//        );
//        return calculatedChecksum.equals(notification.getCheckSum());
//    }
//
//    @Override
//    public String getApiKey() {
//        return paymeeConfig.getApiKey();
//    }
//}