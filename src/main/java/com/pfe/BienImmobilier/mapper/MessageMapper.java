package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.model.MessageDTO;
import com.pfe.BienImmobilier.entities.Message;
import com.pfe.BienImmobilier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final UserRepository userRepository;

    public MessageDTO toDto(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setTimestamp(message.getTimestamp());
        dto.setBienId(message.getBienId());

        // Optionnel : ajouter les noms
        userRepository.findById(message.getSenderId()).ifPresent(user ->
                dto.setSenderName(user.getNom() + " " + user.getPrenom()));

        userRepository.findById(message.getRecipientId()).ifPresent(user ->
                dto.setRecipientName(user.getNom() + " " + user.getPrenom()));

        return dto;
    }

    public Message toEntity(MessageDTO dto) {
        Message message = new Message();
        message.setId(dto.getId());
        message.setSenderId(dto.getSenderId());
        message.setRecipientId(dto.getRecipientId());
        message.setContent(dto.getContent());
        message.setRead(dto.isRead());
        message.setTimestamp(dto.getTimestamp());
        message.setBienId(dto.getBienId());
        return message;
    }
}