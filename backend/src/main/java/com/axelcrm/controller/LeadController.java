package com.axelcrm.controller;

import com.axelcrm.dto.LeadRequest;
import com.axelcrm.dto.LeadResponse;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.LeadConversionService;
import com.axelcrm.service.LeadScoringService;
import com.axelcrm.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Endpoints for managing customer prospects and leads")
public class LeadController {

    private final LeadService leadService;
    private final LeadConversionService leadConversionService;
    private final LeadScoringService leadScoringService;

    @GetMapping
    @Operation(summary = "List all leads in the organization")
    public ResponseEntity<Page<LeadResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadService.findAll(organizationId, pageable));
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "Convert a lead into a client")
    public ResponseEntity<ClientResponse> convert(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(leadConversionService.convertLeadToClient(organizationId, id, userId));
    }

    @PostMapping("/{id}/recalculate-score")
    @Operation(summary = "Recalculate the lead score based on source, value, and engagement")
    public ResponseEntity<Map<String, Integer>> recalculateScore(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        int score = leadScoringService.recalculate(organizationId, id).getScore();
        return ResponseEntity.ok(Map.of("score", score));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a lead by ID")
    public ResponseEntity<LeadResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new lead")
    public ResponseEntity<LeadResponse> create(@Valid @RequestBody LeadRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing lead")
    public ResponseEntity<LeadResponse> update(@PathVariable UUID id, @Valid @RequestBody LeadRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a lead")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        leadService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
