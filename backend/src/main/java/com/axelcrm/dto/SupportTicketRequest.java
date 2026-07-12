package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * DTO for creating or updating a SupportTicket.
 */
@Schema(description = "Support ticket request payload")
public record SupportTicketRequest(
    @NotBlank
    @Schema(description = "Ticket subject")
    String subject,

    @Schema(description = "Ticket description")
    String description,

    @Schema(description = "Associated client ID")
    UUID clientId,

    @Schema(description = "Assigned user ID")
    UUID assignedToId
) {
}
