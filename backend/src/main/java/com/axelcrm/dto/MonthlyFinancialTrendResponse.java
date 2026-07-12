package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.YearMonth;
import java.util.List;

@Schema(description = "Monthly financial trend series")
public record MonthlyFinancialTrendResponse(
    @Schema(description = "Start period (inclusive)") YearMonth startPeriod,
    @Schema(description = "End period (inclusive)") YearMonth endPeriod,
    @Schema(description = "Monthly entries") List<MonthlyFinanceEntry> entries
) {
    public record MonthlyFinanceEntry(
        @Schema(description = "Period (yyyy-MM)") YearMonth period,
        @Schema(description = "Total revenue for the period") java.math.BigDecimal revenue,
        @Schema(description = "Total expenses for the period") java.math.BigDecimal expenses,
        @Schema(description = "Net result (revenue - expenses)") java.math.BigDecimal net
    ) {
    }
}
