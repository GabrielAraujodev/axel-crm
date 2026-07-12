package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating or updating a Deal.
 */
public record DealRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 4000) String description,
    BigDecimal value,
    @NotNull UUID pipelineId,
    @NotNull UUID stageId,
    @NotNull UUID clientId,
    UUID contactId,
    UUID assignedToUserId,
    LocalDate expectedCloseDate
) {
}
