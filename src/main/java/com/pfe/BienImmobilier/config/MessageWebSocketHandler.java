package com.pfe.BienImmobilier.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Message;
import com.pfe.BienImmobilier.model.MessageDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.impl.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// MessageWebSocketHandler.java
@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final MessageServiceImpl messageService;
    private final UserRepository userRepository;

    public MessageWebSocketHandler(ObjectMapper objectMapper,
                                   MessageServiceImpl messageService,
                                   UserRepository userRepository) {
        this.objectMapper = objectMapper;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = session.getAttributes().get("userId").toString();
        if (userId != null) {
            sessions.put(userId, session);
            logger.info("Nouvelle connexion WebSocket - UserID: {}", userId);

            // Envoyer les messages non lus
            sendUnreadMessages(userId);
        }
    }

    private void sendUnreadMessages(String userIdStr) {
        Long userId = Long.parseLong(userIdStr);
        List<Message> unreadMessages = messageService.getUnreadMessages(userId);

        unreadMessages.forEach(msg -> {
            try {
                WebSocketSession session = sessions.get(userIdStr);
                if (session != null && session.isOpen()) {
                    MessageDTO dto = convertToDTO(msg);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
                    messageService.markAsRead(msg.getId());
                }
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi des messages non lus", e);
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            String type = jsonNode.path("type").asText();

            switch (type) {
                case "message":
                    handleChatMessage(session, jsonNode);
                    break;
                case "typing":
                    handleTypingNotification(session, jsonNode);
                    break;
                default:
                    logger.warn("Type de message WebSocket non reconnu: {}", type);
            }
        } catch (Exception e) {
            logger.error("Erreur de traitement du message WebSocket", e);
        }
    }
    private void handleTypingNotification(WebSocketSession session, JsonNode jsonNode) {
        Long recipientId = jsonNode.path("recipientId").asLong();
        String typingStatus = jsonNode.path("status").asText(); // "typing" ou "stopped"

        WebSocketSession recipientSession = sessions.get(recipientId.toString());
        if (recipientSession != null && recipientSession.isOpen()) {
            try {
                ObjectNode response = objectMapper.createObjectNode();
                response.put("type", "typing");
                response.put("senderId", session.getAttributes().get("userId").toString());
                response.put("status", typingStatus);

                recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi du statut de frappe", e);
            }
        }
    }

    private void handleChatMessage(WebSocketSession session, JsonNode jsonNode) throws Exception {
        Long senderId = Long.parseLong(session.getAttributes().get("userId").toString());
        Long recipientId = jsonNode.path("recipientId").asLong();
        String content = jsonNode.path("content").asText();
        Long bienId = jsonNode.path("bienId").asLong();

        // Sauvegarder le message
        Message savedMessage = messageService.saveMessage(senderId, recipientId, content, bienId);
        MessageDTO dto = convertToDTO(savedMessage);

        // Envoyer au destinataire si connecté
        WebSocketSession recipientSession = sessions.get(recipientId.toString());
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
        }

        // Confirmation à l'expéditeur
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = session.getAttributes().get("userId").toString();
        if (userId != null) {
            sessions.remove(userId);
            logger.info("Déconnexion WebSocket - UserID: {}", userId);
        }
    }
}