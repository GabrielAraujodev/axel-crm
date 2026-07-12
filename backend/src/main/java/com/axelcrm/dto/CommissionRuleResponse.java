package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for CommissionRule responses.
 */
public record CommissionRuleResponse(
    UUID id,
    String name,
    String description,
    BigDecimal percentage,
    BigDecimal minValue,
    BigDecimal maxValue,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
