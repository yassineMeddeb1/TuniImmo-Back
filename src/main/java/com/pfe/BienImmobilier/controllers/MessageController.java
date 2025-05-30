package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Message;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.ContactDTO;
import com.pfe.BienImmobilier.model.MessageDTO;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import com.pfe.BienImmobilier.services.impl.MessageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// MessageController.java
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageServiceImpl messageService;
    private final JwtUtil jwtUtils;
    private final UserRepository utilisateurRepository;
    private final HttpServletRequest request;
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long contactId,
            @RequestParam(required = false) Long bienId) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Long userId = utilisateur.getId();
        return ResponseEntity.ok(
                messageService.getConversation(userId, contactId, bienId)
        );
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactDTO>> getContacts() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Long userId = utilisateur.getId();;
        return ResponseEntity.ok(messageService.getContacts(userId));
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<Void> markAsRead(@RequestBody List<Long> messageIds) {
        messageIds.forEach(messageService::markAsRead);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        Message savedMessage = messageService.saveMessage(
                messageDTO.getSenderId(),
                messageDTO.getRecipientId(),
                messageDTO.getContent(),
                messageDTO.getBienId()
        );
        return ResponseEntity.ok(convertToDTO(savedMessage));
    }
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setTimestamp(message.getTimestamp());
        dto.setBienId(message.getBienId());
        return dto;
    }
}
