package com.axelcrm.controller;

import com.axelcrm.dto.PartnerRequest;
import com.axelcrm.dto.PartnerResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.PartnerService;
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
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Endpoints for managing partners/referrers")
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    @Operation(summary = "List all partners with KPIs")
    public ResponseEntity<Page<PartnerResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(partnerService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a partner by ID")
    public ResponseEntity<PartnerResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(partnerService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new partner")
    public ResponseEntity<PartnerResponse> create(@Valid @RequestBody PartnerRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(partnerService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing partner")
    public ResponseEntity<PartnerResponse> update(@PathVariable UUID id, @Valid @RequestBody PartnerRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(partnerService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a partner")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        partnerService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
