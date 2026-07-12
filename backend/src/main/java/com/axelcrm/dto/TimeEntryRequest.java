package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.axelcrm.auth.entity.User;

/**
 * DTO for creating or updating a TimeEntry.
 */
@Schema(description = "Time entry request payload")
public record TimeEntryRequest(
    @NotNull
    @Schema(description = "User ID")
    UUID userId,

    @NotNull
    @Schema(description = "Start time")
    LocalDateTime startTime,

    @Schema(description = "End time")
    LocalDateTime endTime,

    @Schema(description = "Duration in minutes")
    Integer durationMinutes,

    @Schema(description = "Entry description")
    String description,

    @Schema(description = "Associated task ID")
    UUID taskId,

    @Schema(description = "Associated project ID")
    UUID projectId,

    @Schema(description = "Hourly rate")
    BigDecimal hourlyRate
) {
}
