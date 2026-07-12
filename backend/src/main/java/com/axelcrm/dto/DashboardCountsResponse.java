package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing aggregated entity counts for the dashboard.
 */
@Schema(description = "Aggregated count metrics for key dashboard entities")
public record DashboardCountsResponse(
    @Schema(description = "Total number of clients") long clientsCount,
    @Schema(description = "Total number of leads") long leadsCount,
    @Schema(description = "Total number of deals") long dealsCount,
    @Schema(description = "Total number of projects") long projectsCount,
    @Schema(description = "Total number of tasks") long tasksCount,
    @Schema(description = "Total number of proposals") long proposalsCount,
    @Schema(description = "Total number of campaigns") long campaignsCount,
    @Schema(description = "Total number of support tickets") long supportTicketsCount
) {
}
