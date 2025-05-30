package com.pfe.BienImmobilier.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private boolean read;
    private LocalDateTime timestamp;
    private Long bienId;
    private String senderName;
    private String recipientName;
}