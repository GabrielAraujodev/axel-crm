package com.axelcrm.dto;

import com.axelcrm.entity.enums.TaskStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Task responses.
 */
public record TaskResponse(
    UUID id,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime dueDate,
    LocalDateTime completedAt,
    UUID assignedToUserId,
    String assignedToName,
    UUID leadId,
    UUID clientId,
    UUID dealId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
