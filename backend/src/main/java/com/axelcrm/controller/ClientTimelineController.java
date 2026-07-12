package com.axelcrm.controller;

import com.axelcrm.dto.TimelineItemResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ClientTimelineService;
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
@RequestMapping("/api/v1/clients/{clientId}/timeline")
@RequiredArgsConstructor
@Tag(name = "Client Timeline", description = "Endpoints for retrieving unified client notes and logs")
public class ClientTimelineController {

    private final ClientTimelineService clientTimelineService;

    @GetMapping
    @Operation(summary = "Get unified timeline (notes + audit logs) for a client")
    public ResponseEntity<List<TimelineItemResponse>> getTimeline(@PathVariable UUID clientId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientTimelineService.getTimeline(organizationId, clientId));
    }
}
