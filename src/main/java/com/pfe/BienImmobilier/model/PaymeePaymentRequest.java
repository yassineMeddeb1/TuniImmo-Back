package com.pfe.BienImmobilier.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@Builder
public class PaymeePaymentRequest {
    @NotNull
    @Positive
    private Double amount;



    @NotBlank
    private String note;

    // Les champs suivants seront remplis automatiquement par le backend
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String order_id;
    private String webhook_url;
    private String return_url;
    private String cancel_url;
}