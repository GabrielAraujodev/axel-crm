package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Integration responses.
 */
public record IntegrationResponse(
    UUID id,
    String name,
    String provider,
    String credentials,
    String webhookUrl,
    String apiKey,
    Boolean active,
    LocalDateTime lastSyncAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
