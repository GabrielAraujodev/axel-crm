package com.axelcrm.dto;

import java.math.BigDecimal;

public record ReportItemResponse(
    String accountCode,
    String accountName,
    BigDecimal total
) {
}
