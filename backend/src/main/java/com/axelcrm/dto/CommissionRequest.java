package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating or updating a Commission.
 */
@Schema(description = "Commission request payload")
public record CommissionRequest(
    @NotNull
    @Schema(description = "Associated deal ID")
    UUID dealId,

    @Schema(description = "User receiving the commission")
    UUID userId,

    @Schema(description = "Commission rule ID")
    UUID ruleId,

    @Schema(description = "Partner receiving the commission")
    UUID partnerId,

    @Schema(description = "Role in the commission (e.g. CAPTURE, SELLER, etc.)")
    String role,

    @Schema(description = "Date when commission becomes available")
    java.time.LocalDate availableAt,

    @NotNull
    @Schema(description = "Deal value used for calculation")
    BigDecimal dealValue,

    @NotNull
    @Schema(description = "Calculated commission amount")
    BigDecimal amount
) {
}
