package com.pfe.BienImmobilier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.BienImmobilier.model.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.put(userId, session);
            logger.info("WebSocket connection established for user: {}", userId);

            // Envoyer une notification de test
            NotificationDTO connectionNotification = new NotificationDTO();
            connectionNotification.setMessage("Connexion WebSocket établie");

            sendNotificationToUser(userId, connectionNotification);
        }
    }

    public void sendNotificationToUser(String userId, NotificationDTO notification) throws IOException {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            synchronized (session) {
                String message = objectMapper.writeValueAsString(notification);
                session.sendMessage(new TextMessage(message));
                logger.info("Notification sent to user {}: {}", userId, message);
            }
        } else {
            logger.warn("No active session found for user: {}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        logger.debug("Message received from user {}: {}", userId, message.getPayload());

        // Gérer les messages ping/pong
        if (message.getPayload().contains("\"type\":\"ping\"")) {
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
            logger.info("WebSocket connection closed for user: {}", userId);
        }
    }

    public Map<String, WebSocketSession> getSessions() {
        return Collections.unmodifiableMap(sessions);
    }
}