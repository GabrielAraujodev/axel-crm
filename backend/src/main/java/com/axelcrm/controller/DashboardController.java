package com.axelcrm.controller;

import com.axelcrm.dto.DashboardCountsResponse;
import com.axelcrm.dto.DashboardSummaryResponse;
import com.axelcrm.dto.LeadAnalyticsResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse;
import com.axelcrm.dto.ProjectProfitabilityResponse;
import com.axelcrm.dto.ProposalAnalyticsResponse;
import com.axelcrm.dto.SalesAnalyticsResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.AnalyticsService;
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

/**
 * Controller exposing aggregated endpoints for the system dashboard.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for retrieving aggregated dashboard statistics and summaries")
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get the complete dashboard performance summary (KPIs)")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getDashboardSummary(organizationId));
    }

    @GetMapping("/counts")
    @Operation(summary = "Get the aggregated record counts for the main CRM entities")
    public ResponseEntity<DashboardCountsResponse> getDashboardCounts() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getDashboardCounts(organizationId));
    }

    @GetMapping("/sales")
    @Operation(summary = "Get sales performance analytics — pipeline, win/loss, weighted value")
    public ResponseEntity<SalesAnalyticsResponse> getSalesAnalytics() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getSalesAnalytics(organizationId));
    }

    @GetMapping("/leads")
    @Operation(summary = "Get lead analytics — funnel distribution, source performance, conversion rate")
    public ResponseEntity<LeadAnalyticsResponse> getLeadAnalytics() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getLeadAnalytics(organizationId));
    }

    @GetMapping("/financial-trend")
    @Operation(summary = "Get monthly financial trend (revenue/expenses/net) over a date range")
    public ResponseEntity<MonthlyFinancialTrendResponse> getFinancialTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getFinancialTrend(organizationId, start, end));
    }

    @GetMapping("/projects")
    @Operation(summary = "Get project profitability report — budget vs cost per project")
    public ResponseEntity<ProjectProfitabilityResponse> getProjectProfitability() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getProjectProfitability(organizationId));
    }

    @GetMapping("/proposals")
    @Operation(summary = "Get proposal analytics — win rate, status distribution, average amount")
    public ResponseEntity<ProposalAnalyticsResponse> getProposalAnalytics() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getProposalAnalytics(organizationId));
    }
}
