package com.axelcrm.controller;

import com.axelcrm.dto.LeadDetailDtos.LeadNoteRequest;
import com.axelcrm.dto.LeadDetailDtos.LeadNoteResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.LeadNoteService;
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
@RequestMapping("/api/v1/leads/{leadId}/notes")
@RequiredArgsConstructor
@Tag(name = "Lead Notes", description = "Endpoints for managing notes on a specific Lead")
public class LeadNoteController {

    private final LeadNoteService leadNoteService;

    @GetMapping
    @Operation(summary = "List all notes for a lead")
    public ResponseEntity<List<LeadNoteResponse>> findNotesByLeadId(@PathVariable UUID leadId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(leadNoteService.findNotesByLeadId(organizationId, leadId));
    }

    @PostMapping
    @Operation(summary = "Add a new note to a lead")
    public ResponseEntity<LeadNoteResponse> createNote(
            @PathVariable UUID leadId,
            @Valid @RequestBody LeadNoteRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(leadNoteService.createNote(organizationId, leadId, request, userId));
    }

    @DeleteMapping("/{noteId}")
    @Operation(summary = "Soft delete a note from a lead")
    public ResponseEntity<Void> deleteNote(
            @PathVariable UUID leadId,
            @PathVariable UUID noteId) {
        UUID organizationId = TenantContext.getOrganizationId();
        leadNoteService.deleteNote(organizationId, leadId, noteId);
        return ResponseEntity.noContent().build();
    }
}
