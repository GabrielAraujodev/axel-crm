package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for creating or updating a Notification.
 */
@Schema(description = "Notification request payload")
public record NotificationRequest(
    @NotNull
    @Schema(description = "Target user ID")
    UUID userId,

    @NotBlank
    @Schema(description = "Notification title")
    String title,

    @NotBlank
    @Schema(description = "Notification message")
    String message,

    @Schema(description = "Related entity type", example = "Deal")
    String entityType,

    @Schema(description = "Related entity ID")
    String entityId
) {
}
