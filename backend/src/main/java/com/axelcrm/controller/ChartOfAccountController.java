package com.axelcrm.controller;

import com.axelcrm.dto.ChartOfAccountRequest;
import com.axelcrm.dto.ChartOfAccountResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ChartOfAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chart-of-accounts")
@RequiredArgsConstructor
@Tag(name = "Chart of Accounts", description = "Endpoints for managing the hierarchical Chart of Accounts (Plano de Contas)")
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;

    @GetMapping("/tree")
    @Operation(summary = "Get the hierarchical tree of accounts")
    public ResponseEntity<List<ChartOfAccountResponse>> findTree() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(chartOfAccountService.findTree(organizationId));
    }

    @GetMapping
    @Operation(summary = "Get all accounts in a flat list ordered by code")
    public ResponseEntity<List<ChartOfAccountResponse>> findAllFlat() {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(chartOfAccountService.findAllFlat(organizationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an account by ID")
    public ResponseEntity<ChartOfAccountResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(chartOfAccountService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<ChartOfAccountResponse> create(@Valid @RequestBody ChartOfAccountRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(chartOfAccountService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing account")
    public ResponseEntity<ChartOfAccountResponse> update(@PathVariable UUID id, @Valid @RequestBody ChartOfAccountRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(chartOfAccountService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an account")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        chartOfAccountService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    @Operation(summary = "Import accounts in bulk from a CSV file")
    public ResponseEntity<Void> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        UUID organizationId = TenantContext.getOrganizationId();
        chartOfAccountService.importCsv(organizationId, file.getInputStream());
        return ResponseEntity.ok().build();
    }
}
