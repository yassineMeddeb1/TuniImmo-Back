package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Récupère la conversation entre deux utilisateurs (optionnellement liée à un bien)
    @Query("SELECT m FROM Message m WHERE " +
            "((m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1)) " +
            "AND (:bienId IS NULL OR m.bienId = :bienId) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversation(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("bienId") Long bienId);

    // Récupère les contacts d'un utilisateur avec le dernier message et le nombre de non-lus
    @Query(value =
            "SELECT " +
                    "  CASE WHEN m.sender_id = :userId THEN m.recipient_id ELSE m.sender_id END as contact_id, " +
                    "  CASE WHEN m.sender_id = :userId THEN u2.nom ELSE u1.nom END as contact_name, " +
                    "  m.content as last_message, " +
                    "  m.timestamp as last_message_time, " +
                    "  (SELECT COUNT(*) FROM messages m2 WHERE m2.recipient_id = :userId AND m2.sender_id = contact_id AND m2.is_read = false) as unread_count, " +
                    "  m.bien_id " +
                    "FROM messages m " +
                    "LEFT JOIN utilisateurs u1 ON m.sender_id = u1.id " +
                    "LEFT JOIN utilisateurs u2 ON m.recipient_id = u2.id " +
                    "WHERE m.id IN ( " +
                    "  SELECT MAX(m3.id) FROM messages m3 " +
                    "  WHERE m3.sender_id = :userId OR m3.recipient_id = :userId " +
                    "  GROUP BY CASE WHEN m3.sender_id = :userId THEN m3.recipient_id ELSE m3.sender_id END " +
                    ") " +
                    "ORDER BY last_message_time DESC",
            nativeQuery = true)
    List<Object[]> findContacts(@Param("userId") Long userId);

    // Messages non lus pour un utilisateur
    List<Message> findByRecipientIdAndIsReadFalse(Long recipientId);

    // Marquer un message comme lu
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    void markAsRead(@Param("messageId") Long messageId);
}