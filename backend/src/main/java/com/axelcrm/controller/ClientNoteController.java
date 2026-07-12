package com.axelcrm.controller;

import com.axelcrm.dto.ClientNoteRequest;
import com.axelcrm.dto.ClientNoteResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ClientNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/notes")
@RequiredArgsConstructor
@Tag(name = "Client Notes", description = "Endpoints for managing notes linked to clients")
public class ClientNoteController {

    private final ClientNoteService clientNoteService;

    @GetMapping
    @Operation(summary = "Get all notes for a specific client")
    public ResponseEntity<List<ClientNoteResponse>> findByClient(@PathVariable UUID clientId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientNoteService.findByClient(organizationId, clientId));
    }

    @PostMapping
    @Operation(summary = "Add a note to a client")
    public ResponseEntity<ClientNoteResponse> create(
            @PathVariable UUID clientId,
            @Valid @RequestBody ClientNoteRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(clientNoteService.create(organizationId, clientId, request, userId));
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note")
    public ResponseEntity<Void> delete(@PathVariable UUID clientId, @PathVariable UUID noteId) {
        UUID organizationId = TenantContext.getOrganizationId();
        clientNoteService.delete(organizationId, noteId);
        return ResponseEntity.noContent().build();
    }
}
