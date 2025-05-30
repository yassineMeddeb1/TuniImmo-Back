package com.pfe.BienImmobilier.model;


import lombok.Data;

@Data

public class PaymeePaymentStatus {
    private String token;
    private String check_sum;
    private Boolean payment_status;
    private String order_id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String note;
    private Double amount;
    private Long transaction_id;
    private Double received_amount;
    private Double cost;

    public boolean isSuccess() {
        return Boolean.TRUE.equals(payment_status);
    }
}