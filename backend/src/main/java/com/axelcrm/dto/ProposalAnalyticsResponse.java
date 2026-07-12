package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Proposal analytics — win rate, status distribution, average value")
public record ProposalAnalyticsResponse(
    @Schema(description = "Total proposals") long totalProposals,
    @Schema(description = "Proposals grouped by status") Map<String, Long> byStatus,
    @Schema(description = "Number of accepted proposals") long acceptedCount,
    @Schema(description = "Number of rejected proposals") long rejectedCount,
    @Schema(description = "Win rate percentage") BigDecimal winRate,
    @Schema(description = "Average total amount") BigDecimal avgAmount
) {
}
