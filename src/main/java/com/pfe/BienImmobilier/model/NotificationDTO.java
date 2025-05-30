// NotificationDTO.java
package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.ENotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private String date;
    private boolean lu;
    private ENotificationType type;
    private Long reservationId; // Optionnel pour les notifications liées aux réservations

    public NotificationDTO(String message, ENotificationType type) {
        this.message = message;
        this.type = type;
        this.date = LocalDateTime.now().toString();
        this.lu = false;
    }

    public NotificationDTO(String message, ENotificationType type, Long reservationId) {
        this(message, type);
        this.reservationId = reservationId;
    }
}