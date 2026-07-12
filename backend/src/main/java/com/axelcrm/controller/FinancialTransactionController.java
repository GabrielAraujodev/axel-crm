package com.axelcrm.controller;

import com.axelcrm.dto.FinancialTransactionRequest;
import com.axelcrm.dto.FinancialTransactionResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.FinancialTransactionService;
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
@RequestMapping("/api/v1/financial-transactions")
@RequiredArgsConstructor
@Tag(name = "Financial Transactions", description = "Endpoints for managing cash flows, revenue, and expenses")
public class FinancialTransactionController {

    private final FinancialTransactionService financialTransactionService;

    @GetMapping
    @Operation(summary = "List all financial transactions")
    public ResponseEntity<Page<FinancialTransactionResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialTransactionService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a financial transaction by ID")
    public ResponseEntity<FinancialTransactionResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialTransactionService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new financial transaction")
    public ResponseEntity<FinancialTransactionResponse> create(@Valid @RequestBody FinancialTransactionRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialTransactionService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing financial transaction")
    public ResponseEntity<FinancialTransactionResponse> update(@PathVariable UUID id, @Valid @RequestBody FinancialTransactionRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(financialTransactionService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a financial transaction")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        financialTransactionService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
