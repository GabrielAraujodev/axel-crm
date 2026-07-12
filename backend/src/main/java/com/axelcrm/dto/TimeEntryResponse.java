package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for TimeEntry responses.
 */
public record TimeEntryResponse(
    UUID id,
    UUID userId,
    String userName,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer durationMinutes,
    String description,
    UUID taskId,
    String taskTitle,
    UUID projectId,
    String projectName,
    BigDecimal hourlyRate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
