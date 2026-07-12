package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for SupportTicket responses.
 */
public record SupportTicketResponse(
    UUID id,
    String subject,
    String description,
    String status,
    String priority,
    UUID clientId,
    String clientName,
    UUID assignedToUserId,
    String assignedToName,
    UUID createdByUserId,
    String createdByName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
