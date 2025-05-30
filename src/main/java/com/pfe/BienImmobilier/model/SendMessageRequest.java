package com.pfe.BienImmobilier.model;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long recipientId;
    private String content;
    private Long bienId; // Optionnel
}