package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Sales performance analytics — pipeline value, win/loss metrics")
public record SalesAnalyticsResponse(
    @Schema(description = "Total number of deals") long totalDeals,
    @Schema(description = "Number of won deals") long wonDeals,
    @Schema(description = "Number of lost deals") long lostDeals,
    @Schema(description = "Number of open deals") long openDeals,
    @Schema(description = "Win rate percentage") BigDecimal winRate,
    @Schema(description = "Total pipeline value (open deals)") BigDecimal totalPipelineValue,
    @Schema(description = "Weighted pipeline value (value × win probability)") BigDecimal weightedPipelineValue,
    @Schema(description = "Average deal size") BigDecimal avgDealSize,
    @Schema(description = "Pipeline value grouped by stage name") Map<String, BigDecimal> pipelineByStage
) {
}
