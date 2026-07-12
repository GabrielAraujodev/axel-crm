package com.axelcrm.controller;

import com.axelcrm.dto.LegalProcessRequest;
import com.axelcrm.dto.LegalProcessResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.LegalProcessService;
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
@RequestMapping("/api/v1/legal-processes")
@RequiredArgsConstructor
@Tag(name = "Legal Processes", description = "Endpoints for managing judicial processes (Processos Judiciais)")
public class LegalProcessController {

    private final LegalProcessService legalProcessService;

    @GetMapping
    @Operation(summary = "Get all legal processes paginated")
    public ResponseEntity<Page<LegalProcessResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(legalProcessService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a legal process by ID")
    public ResponseEntity<LegalProcessResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(legalProcessService.findById(organizationId, id));
    }

    @GetMapping("/search")
    @Operation(summary = "Simulate searching a legal process in DataJud by CNJ number")
    public ResponseEntity<LegalProcessResponse> searchDataJud(@RequestParam String cnjNumber) {
        return ResponseEntity.ok(legalProcessService.searchDataJud(cnjNumber));
    }

    @PostMapping
    @Operation(summary = "Create a new legal process")
    public ResponseEntity<LegalProcessResponse> create(@Valid @RequestBody LegalProcessRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(legalProcessService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing legal process")
    public ResponseEntity<LegalProcessResponse> update(@PathVariable UUID id, @Valid @RequestBody LegalProcessRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(legalProcessService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a legal process")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        legalProcessService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
