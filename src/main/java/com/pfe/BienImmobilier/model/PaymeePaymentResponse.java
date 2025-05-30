package com.pfe.BienImmobilier.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymeePaymentResponse {
    private Boolean status;
    private String message;
    private Integer code;
    private PaymentData data;

    @Data
    public static class PaymentData {
        private String token;
        @JsonProperty("order_id") private String orderId;
        @JsonProperty("first_name") private String firstName;
        @JsonProperty("last_name") private String lastName;
        private String email;
        private String phone;
        private String note;
        private Double amount;
        @JsonProperty("payment_url") private String paymentUrl;
    }

    public String getToken() {
        return data != null ? data.token : null;
    }

    public String getOrderId() {
        return data != null ? data.orderId : null;
    }

    public String getPaymentUrl() {
        return data != null ? data.paymentUrl : null;
    }
}
