package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for creating an AuditLog entry.
 */
public record AuditLogRequest(
    @NotNull UUID organizationId,
    UUID userId,
    @NotBlank @Size(max = 100) String entityType,
    @Size(max = 100) String entityId,
    @NotBlank @Size(max = 50) String action,
    @Size(max = 10000) String oldValues,
    @Size(max = 10000) String newValues
) {
}
