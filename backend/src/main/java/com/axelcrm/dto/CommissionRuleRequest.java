package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for creating or updating a CommissionRule.
 */
@Schema(description = "Commission rule request payload")
public record CommissionRuleRequest(
    @NotBlank
    @Schema(description = "Rule name", example = "Vendas Internas")
    String name,

    @Schema(description = "Rule description")
    String description,

    @NotNull
    @DecimalMax("1")
    @Schema(description = "Commission percentage between 0 and 1", example = "0.10")
    BigDecimal percentage,

    @Schema(description = "Minimum deal value to apply the rule")
    BigDecimal minValue,

    @Schema(description = "Maximum deal value to apply the rule")
    BigDecimal maxValue,

    @Schema(description = "Whether the rule is active", example = "true")
    boolean active
) {
}
