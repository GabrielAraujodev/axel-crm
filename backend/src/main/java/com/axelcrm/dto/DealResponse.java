package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Deal responses.
 */
public record DealResponse(
    UUID id,
    String title,
    String description,
    BigDecimal value,
    UUID pipelineId,
    String pipelineName,
    UUID stageId,
    String stageName,
    UUID clientId,
    String clientName,
    UUID contactId,
    UUID assignedToUserId,
    String assignedToName,
    LocalDate expectedCloseDate,
    LocalDateTime closedAt,
    Boolean won,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
