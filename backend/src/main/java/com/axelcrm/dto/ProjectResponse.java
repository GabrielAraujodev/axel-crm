package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Project responses.
 */
public record ProjectResponse(
    UUID id,
    String name,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal budget,
    BigDecimal cost,
    String status,
    UUID clientId,
    String clientName,
    UUID managerUserId,
    String managerUserName,
    UUID legalProcessId,
    String legalProcessCnj,
    String cnjNumber,
    String expertType,
    String paymentStatus,
    LocalDate deliveryDeadline,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public ProjectResponse(
        UUID id, String name, String description, LocalDate startDate, LocalDate endDate,
        BigDecimal budget, BigDecimal cost, String status, UUID clientId, String clientName,
        UUID managerUserId, String managerUserName, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, name, description, startDate, endDate, budget, cost, status, clientId, clientName, managerUserId, managerUserName, null, null, null, null, null, null, createdAt, updatedAt);
    }
}
