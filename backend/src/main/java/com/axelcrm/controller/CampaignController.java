package com.axelcrm.controller;

import com.axelcrm.dto.CampaignRequest;
import com.axelcrm.dto.CampaignResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaigns", description = "Endpoints for managing marketing campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    @Operation(summary = "List all campaigns")
    public ResponseEntity<List<CampaignResponse>> findAll() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(campaignService.findAll(organizationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a campaign by ID")
    public ResponseEntity<CampaignResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(campaignService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new campaign")
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(campaignService.create(organizationId, request, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing campaign")
    public ResponseEntity<CampaignResponse> update(@PathVariable UUID id, @Valid @RequestBody CampaignRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(campaignService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a campaign")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        campaignService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "Send a campaign (simulated stub sending logic)")
    public ResponseEntity<CampaignResponse> sendCampaign(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(campaignService.sendCampaign(organizationId, id));
    }
}
