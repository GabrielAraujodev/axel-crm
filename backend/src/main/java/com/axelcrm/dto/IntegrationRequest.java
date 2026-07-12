package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating an Integration.
 */
@Schema(description = "Integration request payload")
public record IntegrationRequest(
    @NotBlank
    @Schema(description = "Integration name", example = "WhatsApp Business")
    String name,

    @NotBlank
    @Schema(description = "Integration provider", example = "Meta")
    String provider,

    @Schema(description = "Integration credentials (encrypted)")
    String credentials,

    @Schema(description = "Webhook URL")
    String webhookUrl,

    @Schema(description = "API key")
    String apiKey,

    @Schema(description = "Whether the integration is active", example = "false")
    boolean active
) {
}
