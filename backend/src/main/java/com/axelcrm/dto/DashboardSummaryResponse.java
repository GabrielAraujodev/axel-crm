package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Dashboard summary response payload.
 */
@Schema(description = "Dashboard summary with key CRM metrics")
public record DashboardSummaryResponse(
    @Schema(description = "Total number of leads") long totalLeads,
    @Schema(description = "Number of leads grouped by stage") Map<String, Long> leadsByStage,
    @Schema(description = "Total number of clients") long totalClients,
    @Schema(description = "Total number of deals") long totalDeals,
    @Schema(description = "Number of open deals") long openDeals,
    @Schema(description = "Number of won deals") long wonDeals,
    @Schema(description = "Total value of the sales pipeline") BigDecimal pipelineValue,
    @Schema(description = "Revenue for the current month") BigDecimal monthlyRevenue,
    @Schema(description = "Expenses for the current month") BigDecimal monthlyExpenses,
    @Schema(description = "Number of pending tasks") long tasksPending,
    @Schema(description = "Number of completed tasks") long tasksCompleted
) {
}
