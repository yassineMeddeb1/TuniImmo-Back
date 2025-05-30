package com.pfe.BienImmobilier.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymeeNotificationDTO {
    private String token;
    @JsonProperty("check_sum") private String checkSum;
    @JsonProperty("payment_status") private Boolean paymentStatus;
    @JsonProperty("order_id") private String orderId;
    @JsonProperty("first_name") private String firstName;
    @JsonProperty("last_name") private String lastName;
    private String email;
    private String phone;
    private String note;
    private Double amount;
    @JsonProperty("transaction_id") private Long transactionId;
    @JsonProperty("received_amount") private Double receivedAmount;
    private Double cost;
}
