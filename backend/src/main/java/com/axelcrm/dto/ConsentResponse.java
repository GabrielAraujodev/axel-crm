package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload representing an LGPD consent record.
 */
public record ConsentResponse(
    UUID id,
    String personEmail,
    String consentType,
    boolean granted,
    String ipAddress,
    String userAgent,
    LocalDateTime consentedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
