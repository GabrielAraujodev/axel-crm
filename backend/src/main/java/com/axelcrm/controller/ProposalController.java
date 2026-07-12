package com.axelcrm.controller;

import com.axelcrm.dto.ProposalRequest;
import com.axelcrm.dto.ProposalResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ProposalService;
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
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
@Tag(name = "Proposals", description = "Endpoints for managing commercial proposals")
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping
    @Operation(summary = "List all proposals in the organization")
    public ResponseEntity<Page<ProposalResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(proposalService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a proposal by ID")
    public ResponseEntity<ProposalResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(proposalService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new proposal")
    public ResponseEntity<ProposalResponse> create(@Valid @RequestBody ProposalRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(proposalService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing proposal")
    public ResponseEntity<ProposalResponse> update(@PathVariable UUID id, @Valid @RequestBody ProposalRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(proposalService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a proposal")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        proposalService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/{token}")
    @Operation(summary = "Get a proposal by public token (no authentication required)")
    public ResponseEntity<ProposalResponse> findByPublicToken(@PathVariable UUID token) {
        return ResponseEntity.ok(proposalService.findByPublicToken(token));
    }

    @PostMapping("/{id}/convert-to-project")
    @Operation(summary = "Convert an accepted proposal to a project")
    public ResponseEntity<Void> convertToProject(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        proposalService.convertToProject(organizationId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Generate PDF for a proposal")
    public ResponseEntity<byte[]> getPdf(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        byte[] pdfBytes = proposalService.generateProposalPdf(organizationId, id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"proposta-" + id + ".pdf\"")
                .body(pdfBytes);
    }

    @GetMapping("/public/{token}/pdf")
    @Operation(summary = "Generate PDF for a proposal by public token (no authentication required)")
    public ResponseEntity<byte[]> getPublicPdf(@PathVariable UUID token) {
        byte[] pdfBytes = proposalService.generatePublicProposalPdf(token);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"proposta-" + token + ".pdf\"")
                .body(pdfBytes);
    }
}
