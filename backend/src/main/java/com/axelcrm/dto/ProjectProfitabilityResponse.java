package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Project profitability report — budget vs cost")
public record ProjectProfitabilityResponse(
    @Schema(description = "Total projects") long totalProjects,
    @Schema(description = "Total budget") BigDecimal totalBudget,
    @Schema(description = "Total cost") BigDecimal totalCost,
    @Schema(description = "Total margin (budget - cost)") BigDecimal totalMargin,
    @Schema(description = "Average margin percentage") BigDecimal avgMarginPercent,
    @Schema(description = "Per-project breakdown") List<ProjectProfitEntry> entries
) {
    public record ProjectProfitEntry(
        @Schema(description = "Project ID") java.util.UUID id,
        @Schema(description = "Project name") String name,
        @Schema(description = "Project status") String status,
        @Schema(description = "Client name") String clientName,
        @Schema(description = "Budget amount") BigDecimal budget,
        @Schema(description = "Cost amount") BigDecimal cost,
        @Schema(description = "Margin (budget - cost)") BigDecimal margin,
        @Schema(description = "Margin percentage") BigDecimal marginPercent
    ) {
    }
}
