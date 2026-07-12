package com.axelcrm.controller;

import com.axelcrm.dto.ReportResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for generating DRE (accrual report) and DFC (cash flow report)")
public class ReportsController {

    private final ReportsService reportsService;

    @GetMapping("/dre")
    @Operation(summary = "Generate DRE (Demonstrativo de Resultado do Exercício) accrual-based report")
    public ResponseEntity<ReportResponse> getDre(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(reportsService.getDre(organizationId, startDate, endDate));
    }

    @GetMapping("/dfc")
    @Operation(summary = "Generate DFC (Demonstrativo de Fluxo de Caixa) cash-based report")
    public ResponseEntity<ReportResponse> getDfc(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(reportsService.getDfc(organizationId, startDate, endDate));
    }
}
