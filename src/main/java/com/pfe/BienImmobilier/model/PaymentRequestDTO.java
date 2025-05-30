// PaymentRequestDTO.java
package com.pfe.BienImmobilier.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    @NotBlank
    private String type;
    @NotNull @Positive
    private Double amount;
    @NotBlank
    private String note;
}