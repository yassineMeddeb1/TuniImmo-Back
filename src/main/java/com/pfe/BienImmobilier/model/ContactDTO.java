package com.pfe.BienImmobilier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor  
public class ContactDTO {
    private Long contactId;
    private String contactName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
    private Long bienId;
    private String bienTitle; // Optionnel


    public ContactDTO(Long contactId, String contactName, String lastMessage, LocalDateTime lastMessageTime, Long unreadCount, Long bienId) {
    this.contactId = contactId;
    this.contactName = contactName;
    this.lastMessage = lastMessage;
    this.lastMessageTime = lastMessageTime;
    this.unreadCount = unreadCount;
    this.bienId = bienId;

    }
}