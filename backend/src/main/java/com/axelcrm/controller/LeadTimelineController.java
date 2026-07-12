package com.axelcrm.controller;

import com.axelcrm.dto.TimelineItemResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.LeadTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads/{leadId}/timeline")
@RequiredArgsConstructor
@Tag(name = "Lead Timeline", description = "Endpoints for retrieving history/timeline for a specific Lead")
public class LeadTimelineController {

    private final LeadTimelineService leadTimelineService;

    @GetMapping
    @Operation(summary = "Get consolidated timeline for a lead")
    public ResponseEntity<List<TimelineItemResponse>> getTimeline(@PathVariable UUID leadId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadTimelineService.getTimeline(organizationId, leadId));
    }
}
