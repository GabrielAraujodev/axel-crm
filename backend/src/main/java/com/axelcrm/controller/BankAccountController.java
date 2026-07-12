package com.axelcrm.controller;

import com.axelcrm.dto.BankAccountRequest;
import com.axelcrm.dto.BankAccountResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.BankAccountService;
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
@RequestMapping("/api/v1/bank-accounts")
@RequiredArgsConstructor
@Tag(name = "Bank Accounts", description = "Endpoints for managing organization bank accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @Operation(summary = "List all bank accounts")
    public ResponseEntity<Page<BankAccountResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(bankAccountService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a bank account by ID")
    public ResponseEntity<BankAccountResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(bankAccountService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new bank account")
    public ResponseEntity<BankAccountResponse> create(@Valid @RequestBody BankAccountRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(bankAccountService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing bank account")
    public ResponseEntity<BankAccountResponse> update(@PathVariable UUID id, @Valid @RequestBody BankAccountRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(bankAccountService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a bank account")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        bankAccountService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
