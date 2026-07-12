package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Contract request payload")
public record ContractRequest(
    @NotBlank
    @Schema(description = "Contract title", example = "Prestação de Serviços Mensais")
    String title,

    @Schema(description = "Human-readable contract number", example = "CT-2025-001")
    String contractNumber,

    @Schema(description = "Contract description")
    String description,

    @NotNull
    @Schema(description = "Client ID")
    UUID clientId,

    @Schema(description = "Related deal ID")
    UUID dealId,

    @NotNull
    @Schema(description = "Contract start date", example = "2025-01-01")
    LocalDate startDate,

    @Schema(description = "Contract end date", example = "2025-12-31")
    LocalDate endDate,

    @Schema(description = "Total contract value")
    BigDecimal value,

    @Schema(description = "Monthly recurring value")
    BigDecimal monthlyValue,

    @Schema(description = "Contract status: DRAFT, ACTIVE, EXPIRED, TERMINATED, RENEWED", example = "DRAFT")
    String status,

    @Schema(description = "Contract terms and conditions")
    String terms,

    @Schema(description = "Internal notes")
    String notes,

    @Schema(description = "Signed by client name")
    String signedByClient,

    @Schema(description = "Whether the contract auto-renews")
    Boolean autoRenew
) {
}
