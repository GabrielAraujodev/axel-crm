package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Commission responses.
 */
public record CommissionResponse(
    UUID id,
    UUID userId,
    String userName,
    UUID partnerId,
    String partnerName,
    String role,
    java.time.LocalDate availableAt,
    UUID dealId,
    String dealTitle,
    UUID ruleId,
    String ruleName,
    BigDecimal dealValue,
    BigDecimal amount,
    LocalDateTime paidAt,
    Boolean paid,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
