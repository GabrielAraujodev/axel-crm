package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Notification responses.
 */
public record NotificationResponse(
    UUID id,
    UUID userId,
    String userName,
    String title,
    String message,
    String entityType,
    String entityId,
    LocalDateTime readAt,
    Boolean read,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
