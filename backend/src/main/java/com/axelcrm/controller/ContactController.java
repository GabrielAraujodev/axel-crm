package com.axelcrm.controller;

import com.axelcrm.dto.ContactRequest;
import com.axelcrm.dto.ContactResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ContactService;
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
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts", description = "Endpoints for managing contacts")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "List all contacts in the organization")
    public ResponseEntity<Page<ContactResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contactService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contact by ID")
    public ResponseEntity<ContactResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contactService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new contact")
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody ContactRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contactService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contact")
    public ResponseEntity<ContactResponse> update(@PathVariable UUID id, @Valid @RequestBody ContactRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(contactService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a contact")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        contactService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
