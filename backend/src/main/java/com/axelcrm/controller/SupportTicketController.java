package com.axelcrm.controller;

import com.axelcrm.dto.SupportTicketRequest;
import com.axelcrm.dto.SupportTicketResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/support-tickets")
@RequiredArgsConstructor
@Tag(name = "Support Tickets", description = "Endpoints for managing customer support tickets and status")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @GetMapping
    @Operation(summary = "List all support tickets")
    public ResponseEntity<Page<SupportTicketResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(supportTicketService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a support ticket by ID")
    public ResponseEntity<SupportTicketResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(supportTicketService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new support ticket")
    public ResponseEntity<SupportTicketResponse> create(@Valid @RequestBody SupportTicketRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(supportTicketService.create(organizationId, request, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing support ticket")
    public ResponseEntity<SupportTicketResponse> update(@PathVariable UUID id, @Valid @RequestBody SupportTicketRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(supportTicketService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a support ticket")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        supportTicketService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
