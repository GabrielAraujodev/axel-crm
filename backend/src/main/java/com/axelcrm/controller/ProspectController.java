package com.axelcrm.controller;

import com.axelcrm.dto.ProspectRequest;
import com.axelcrm.dto.ProspectResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ProspectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prospects")
@RequiredArgsConstructor
@Tag(name = "Prospects", description = "Endpoints for managing prospects (pre-leads)")
public class ProspectController {

    private final ProspectService prospectService;

    @GetMapping
    @Operation(summary = "List all prospects in the organization")
    public ResponseEntity<Page<ProspectResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(prospectService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a prospect by ID")
    public ResponseEntity<ProspectResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(prospectService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new prospect")
    public ResponseEntity<ProspectResponse> create(@Valid @RequestBody ProspectRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(prospectService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing prospect")
    public ResponseEntity<ProspectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProspectRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(prospectService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a prospect")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        prospectService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/promote")
    @Operation(summary = "Promote a prospect to a lead")
    public ResponseEntity<ProspectResponse> promoteToLead(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(prospectService.promoteToLead(organizationId, id));
    }
}
