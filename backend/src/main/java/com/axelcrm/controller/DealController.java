package com.axelcrm.controller;

import com.axelcrm.dto.DealRequest;
import com.axelcrm.dto.DealResponse;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.dto.StageTransitionRequest;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.DealService;
import com.axelcrm.service.ProjectService;
import com.axelcrm.service.StageTransitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deals")
@RequiredArgsConstructor
@Tag(name = "Deals", description = "Endpoints for managing deals/opportunities")
public class DealController {

    private final DealService dealService;
    private final ProjectService projectService;
    private final StageTransitionService stageTransitionService;

    @GetMapping
    @Operation(summary = "List all deals in the organization")
    public ResponseEntity<Page<DealResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(dealService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a deal by ID")
    public ResponseEntity<DealResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(dealService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new deal")
    public ResponseEntity<DealResponse> create(@Valid @RequestBody DealRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(dealService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing deal")
    public ResponseEntity<DealResponse> update(@PathVariable UUID id, @Valid @RequestBody DealRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(dealService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a deal")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        dealService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/convert-to-project")
    @Operation(summary = "Convert a won deal to a project")
    public ResponseEntity<ProjectResponse> convertToProject(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(projectService.createFromDeal(organizationId, id));
    }

    @PostMapping("/{id}/transition")
    @Operation(summary = "Move a deal to a new pipeline stage")
    public ResponseEntity<DealResponse> transition(
            @PathVariable UUID id,
            @Valid @RequestBody StageTransitionRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = TenantContext.getUserId();
        return ResponseEntity.ok(stageTransitionService.transition(
                organizationId, userId, id, request.stageId(), request.reason()));
    }

    @PostMapping("/{id}/mark-lost")
    @Operation(summary = "Mark a deal as lost")
    public ResponseEntity<DealResponse> markLost(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = TenantContext.getUserId();
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(stageTransitionService.markLost(organizationId, userId, id, reason));
    }

    @PostMapping("/{id}/reopen")
    @Operation(summary = "Reopen a closed deal")
    public ResponseEntity<DealResponse> reopen(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = TenantContext.getUserId();
        return ResponseEntity.ok(stageTransitionService.reopen(organizationId, userId, id));
    }
}
