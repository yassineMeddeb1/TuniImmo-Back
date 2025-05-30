package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.config.NotificationWebSocketHandler;
import com.pfe.BienImmobilier.entities.Notification;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.NotificationDTO;
import com.pfe.BienImmobilier.repository.NotificationRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import com.pfe.BienImmobilier.services.inter.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketHandler webSocketHandler;

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotificationToUser(
            @RequestParam Long userId,
            @RequestBody NotificationDTO notificationDto) throws IOException {

        // 1. Trouver l'utilisateur destinataire
        Utilisateur destinataire = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Sauvegarder en base et envoyer via WebSocket
        notificationService.envoyerNotification(destinataire, notificationDto);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@RequestBody List<Long> ids) {
        notificationService.marquerToutesCommeLues(ids);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/paginated")
    public ResponseEntity<Page<Notification>> getPaginatedNotifications(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String token = extractToken(request);
        String email = jwtUtil.extractEmail(token);
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateEnvoi").descending());
        Page<Notification> notifications = notificationRepository.findByDestinataire(user, pageable);

        return ResponseEntity.ok(notifications);
    }


    @GetMapping
    public ResponseEntity<List<Notification>> getUnreadNotifications(HttpServletRequest request) {
        String token = extractToken(request);
        String email = jwtUtil.extractEmail(token);
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(notificationService.getNotificationsNonLues(user));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/{userId}")
    public ResponseEntity<String> sendTestNotification(@PathVariable Long userId) {
        try {
            NotificationDTO testNotification = new NotificationDTO();
            testNotification.setMessage("Ceci est une notification de test");
            testNotification.setDate(LocalDateTime.now().toString());
            testNotification.setLu(false);


            webSocketHandler.sendNotificationToUser(userId.toString(), testNotification);
            return ResponseEntity.ok("Notification de test envoyée à l'utilisateur " + userId);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}