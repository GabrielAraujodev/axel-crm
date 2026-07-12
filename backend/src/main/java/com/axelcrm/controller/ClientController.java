package com.axelcrm.controller;

import com.axelcrm.dto.ClientRequest;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ClientService;
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
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Endpoints for managing clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "List all clients in the organization")
    public ResponseEntity<Page<ClientResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a client by ID")
    public ResponseEntity<ClientResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new client")
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing client")
    public ResponseEntity<ClientResponse> update(@PathVariable UUID id, @Valid @RequestBody ClientRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a client")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        clientService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
