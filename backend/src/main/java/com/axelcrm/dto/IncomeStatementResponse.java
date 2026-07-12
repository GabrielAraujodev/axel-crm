package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DRE — Demonstrativo de Resultado do Exercício.
 * Shows revenue minus expenses to calculate net result.
 */
@Schema(description = "Income statement (DRE)")
public record IncomeStatementResponse(
    @Schema(description = "Report start date")
    LocalDate startDate,

    @Schema(description = "Report end date")
    LocalDate endDate,

    @Schema(description = "Total revenue (incomes)")
    BigDecimal totalRevenue,

    @Schema(description = "Total expenses")
    BigDecimal totalExpenses,

    @Schema(description = "Net result (revenue - expenses)")
    BigDecimal netResult
) {}
