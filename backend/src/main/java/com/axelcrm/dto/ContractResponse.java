package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContractResponse(
    UUID id,
    String title,
    String contractNumber,
    String description,
    UUID clientId,
    String clientName,
    UUID dealId,
    String dealTitle,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal value,
    BigDecimal monthlyValue,
    String status,
    String terms,
    String notes,
    String signedByClient,
    LocalDateTime signedAt,
    LocalDateTime renewedAt,
    boolean autoRenew,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
