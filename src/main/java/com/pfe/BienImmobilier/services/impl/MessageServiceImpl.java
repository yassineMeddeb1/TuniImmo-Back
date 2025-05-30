package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Message;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.exceptions.NotFoundException;
import com.pfe.BienImmobilier.model.ContactDTO;
import com.pfe.BienImmobilier.model.MessageDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.MessageRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// MessageService.java
@Service
@RequiredArgsConstructor
public class MessageServiceImpl {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BienImmobilierRepository bienRepository;

    public Message saveMessage(Long senderId, Long recipientId, String content, Long bienId) {
        Utilisateur sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Expéditeur non trouvé"));
        Utilisateur recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("Destinataire non trouvé"));

        BienImmobilier bien = bienId != null ? bienRepository.findById(bienId).orElse(null) : null;

        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setBienId(bienId);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        return messageRepository.save(message);
    }

    public List<MessageDTO> getConversation(Long userId1, Long userId2, Long bienId) {
        return messageRepository.findConversation(userId1, userId2, bienId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> getContacts(Long userId) {
        List<Object[]> results = messageRepository.findContacts(userId);
        return results.stream().map(this::mapToContactDTO).collect(Collectors.toList());
    }

    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByRecipientIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setRead(message.isRead());
        dto.setBienId(message.getBienId());
        return dto;
    }

    private ContactDTO mapToContactDTO(Object[] result) {
        Long contactId = (Long) result[0];
        String contactName = (String) result[1];
        String lastMessage = (String) result[2];
        Timestamp timestamp = (Timestamp) result[3];
        LocalDateTime lastMessageTime = timestamp.toLocalDateTime();
        Long unreadCount = (Long) result[4];
        Long bienId = result[5] != null ? (Long) result[5] : null;

        return new ContactDTO(
                contactId,
                contactName,
                lastMessage,
                lastMessageTime,
                unreadCount,
                bienId
        );
    }
}