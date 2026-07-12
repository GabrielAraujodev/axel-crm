package com.axelcrm.controller;

import com.axelcrm.dto.DocumentRequest;
import com.axelcrm.dto.DocumentResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.DocumentService;
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
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Endpoints for managing document repository")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    @Operation(summary = "List all documents")
    public ResponseEntity<Page<DocumentResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(documentService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a document by ID")
    public ResponseEntity<DocumentResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(documentService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new document")
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(documentService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing document")
    public ResponseEntity<DocumentResponse> update(@PathVariable UUID id, @Valid @RequestBody DocumentRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(documentService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a document")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        documentService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
