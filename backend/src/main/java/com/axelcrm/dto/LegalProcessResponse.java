package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LegalProcessResponse(
    UUID id,
    String cnjNumber,
    String court,
    LocalDate distributionDate,
    BigDecimal value,
    String status,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
