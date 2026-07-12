package com.axelcrm.dto;

import com.axelcrm.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating or updating a Task.
 */
public record TaskRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 4000) String description,
    TaskStatus status,
    LocalDateTime dueDate,
    @NotNull UUID assignedToUserId,
    UUID leadId,
    UUID clientId,
    UUID dealId
) {
}
