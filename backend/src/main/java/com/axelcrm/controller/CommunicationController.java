package com.axelcrm.controller;

import com.axelcrm.dto.MessageRequest;
import com.axelcrm.dto.MessageResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.CommunicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing REST endpoints for multi-channel communication history.
 */
@RestController
@RequestMapping("/api/v1/communications")
@RequiredArgsConstructor
@Tag(name = "Communications", description = "Endpoints for managing multi-channel communication history logs")
public class CommunicationController {

    private final CommunicationService communicationService;

    @GetMapping
    @Operation(summary = "List all communication messages in the organization")
    public ResponseEntity<Page<MessageResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(communicationService.findAll(organizationId, pageable));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "List all communication messages for a specific lead")
    public ResponseEntity<Page<MessageResponse>> findByLeadId(@PathVariable UUID leadId, Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(communicationService.findByLeadId(organizationId, leadId, pageable));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "List all communication messages for a specific client")
    public ResponseEntity<Page<MessageResponse>> findByClientId(@PathVariable UUID clientId, Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(communicationService.findByClientId(organizationId, clientId, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new communication message log")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody MessageRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(communicationService.create(organizationId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a communication message log")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        communicationService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
