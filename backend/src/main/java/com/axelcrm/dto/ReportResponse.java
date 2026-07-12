package com.axelcrm.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReportResponse(
    List<ReportItemResponse> revenues,
    List<ReportItemResponse> expenses,
    BigDecimal totalRevenues,
    BigDecimal totalExpenses,
    BigDecimal netResult
) {
}
