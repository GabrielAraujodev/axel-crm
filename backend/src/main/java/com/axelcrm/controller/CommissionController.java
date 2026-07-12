package com.axelcrm.controller;

import com.axelcrm.dto.CommissionRequest;
import com.axelcrm.dto.CommissionResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.CommissionService;
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
@RequestMapping("/api/v1/commissions")
@RequiredArgsConstructor
@Tag(name = "Commissions", description = "Endpoints for managing user sales commissions")
public class CommissionController {

    private final CommissionService commissionService;

    @GetMapping
    @Operation(summary = "List all commissions")
    public ResponseEntity<Page<CommissionResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a commission by ID")
    public ResponseEntity<CommissionResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new commission record")
    public ResponseEntity<CommissionResponse> create(@Valid @RequestBody CommissionRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing commission record")
    public ResponseEntity<CommissionResponse> update(@PathVariable UUID id, @Valid @RequestBody CommissionRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a commission record")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        commissionService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
