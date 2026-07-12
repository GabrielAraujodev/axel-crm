package com.axelcrm.controller;

import com.axelcrm.dto.DashboardSummaryResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for retrieving business performance and dashboard summaries")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get the complete dashboard performance summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(analyticsService.getDashboardSummary(organizationId));
    }
}
