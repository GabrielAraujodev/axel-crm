package com.axelcrm.controller;

import com.axelcrm.dto.CommissionRuleRequest;
import com.axelcrm.dto.CommissionRuleResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.CommissionRuleService;
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
@RequestMapping("/api/v1/commission-rules")
@RequiredArgsConstructor
@Tag(name = "Commission Rules", description = "Endpoints for managing rules that calculate commissions for users")
public class CommissionRuleController {

    private final CommissionRuleService commissionRuleService;

    @GetMapping
    @Operation(summary = "List all commission rules")
    public ResponseEntity<Page<CommissionRuleResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionRuleService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a commission rule by ID")
    public ResponseEntity<CommissionRuleResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionRuleService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new commission rule")
    public ResponseEntity<CommissionRuleResponse> create(@Valid @RequestBody CommissionRuleRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionRuleService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing commission rule")
    public ResponseEntity<CommissionRuleResponse> update(@PathVariable UUID id, @Valid @RequestBody CommissionRuleRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(commissionRuleService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a commission rule")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        commissionRuleService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
