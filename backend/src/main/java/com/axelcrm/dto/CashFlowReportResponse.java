package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DFC — Demonstrativo de Fluxo de Caixa.
 * Shows cash inflows and outflows grouped by period.
 */
@Schema(description = "Cash flow statement (DFC)")
public record CashFlowReportResponse(
    @Schema(description = "Report start date")
    LocalDate startDate,

    @Schema(description = "Report end date")
    LocalDate endDate,

    @Schema(description = "Period breakdown of cash flows")
    List<CashFlowPeriod> periods,

    @Schema(description = "Total inflows for the period")
    BigDecimal totalInflows,

    @Schema(description = "Total outflows for the period")
    BigDecimal totalOutflows,

    @Schema(description = "Net cash flow (inflows - outflows)")
    BigDecimal netCashFlow
) {
    public record CashFlowPeriod(
        @Schema(description = "Period label (e.g. 2026-01)")
        String period,

        @Schema(description = "Total inflows in this period")
        BigDecimal inflows,

        @Schema(description = "Total outflows in this period")
        BigDecimal outflows,

        @Schema(description = "Net flow for this period")
        BigDecimal netFlow
    ) {}
}
