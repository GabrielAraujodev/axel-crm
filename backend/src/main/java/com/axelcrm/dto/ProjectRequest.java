package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating or updating a Project.
 */
public record ProjectRequest(
    @NotBlank @Size(max = 200) String name,
    @Size(max = 4000) String description,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal budget,
    BigDecimal cost,
    @Size(max = 50) String status,
    @NotNull UUID clientId,
    UUID managerUserId,
    UUID legalProcessId,
    String cnjNumber,
    String expertType,
    String paymentStatus,
    LocalDate deliveryDeadline
) {
    public ProjectRequest(
        String name, String description, LocalDate startDate, LocalDate endDate,
        BigDecimal budget, BigDecimal cost, String status, UUID clientId, UUID managerUserId
    ) {
        this(name, description, startDate, endDate, budget, cost, status, clientId, managerUserId, null, null, null, null, null);
    }
}
