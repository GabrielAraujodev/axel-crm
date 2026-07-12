package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for AuditLog responses.
 */
public record AuditLogResponse(
    UUID id,
    UUID organizationId,
    UUID userId,
    String userName,
    String entityType,
    String entityId,
    String action,
    String oldValues,
    String newValues,
    LocalDateTime createdAt
) {
}
