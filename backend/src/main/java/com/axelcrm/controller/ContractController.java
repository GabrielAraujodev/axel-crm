package com.axelcrm.controller;

import com.axelcrm.dto.ContractRequest;
import com.axelcrm.dto.ContractResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ContractService;
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
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Endpoints for managing client contracts")
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @Operation(summary = "List all contracts")
    public ResponseEntity<Page<ContractResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contractService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contract by ID")
    public ResponseEntity<ContractResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contractService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new contract")
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contractService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contract")
    public ResponseEntity<ContractResponse> update(@PathVariable UUID id, @Valid @RequestBody ContractRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contractService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a contract")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        contractService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
