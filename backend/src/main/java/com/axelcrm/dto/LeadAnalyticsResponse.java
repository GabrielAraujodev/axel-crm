package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "Lead analytics — funnel distribution, source performance")
public record LeadAnalyticsResponse(
    @Schema(description = "Total number of leads") long totalLeads,
    @Schema(description = "Leads grouped by stage") Map<String, Long> byStage,
    @Schema(description = "Leads grouped by source") Map<String, Long> bySource,
    @Schema(description = "Number of converted leads") long convertedLeads,
    @Schema(description = "Conversion rate percentage") BigDecimal conversionRate
) {
}
