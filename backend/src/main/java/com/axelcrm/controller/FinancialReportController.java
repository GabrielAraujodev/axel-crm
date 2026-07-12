package com.axelcrm.controller;

import com.axelcrm.dto.CashFlowReportResponse;
import com.axelcrm.dto.IncomeStatementResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.FinancialReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial-reports")
@RequiredArgsConstructor
@Tag(name = "Financial Reports", description = "Endpoints for DFC and DRE reports")
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/cash-flow")
    @Operation(summary = "Generate cash flow statement (DFC)")
    public ResponseEntity<CashFlowReportResponse> generateCashFlow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialReportService.generateCashFlow(organizationId, startDate, endDate));
    }

    @GetMapping("/income-statement")
    @Operation(summary = "Generate income statement (DRE)")
    public ResponseEntity<IncomeStatementResponse> generateIncomeStatement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialReportService.generateIncomeStatement(organizationId, startDate, endDate));
    }
}
