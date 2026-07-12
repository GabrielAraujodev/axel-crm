package com.axelcrm.controller;

import com.axelcrm.dto.ConsentRequest;
import com.axelcrm.dto.ConsentResponse;
import com.axelcrm.dto.LgpdDataExportResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.LgpdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller exposing LGPD compliance endpoints for consent, portability, and deletion.
 */
@RestController
@RequestMapping("/api/v1/lgpd")
@RequiredArgsConstructor
@Tag(name = "LGPD", description = "Endpoints for managing LGPD compliance, consents, data portability, and the right to be forgotten")
public class LgpdController {

    private final LgpdService lgpdService;

    @PostMapping("/consent")
    @Operation(summary = "Save or update a person's consent for a specific processing purpose")
    public ResponseEntity<ConsentResponse> saveConsent(@Valid @RequestBody ConsentRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(lgpdService.saveConsent(organizationId, request));
    }

    @GetMapping("/export")
    @Operation(summary = "Export all personal data associated with an email for portability")
    public ResponseEntity<LgpdDataExportResponse> exportData(@RequestParam String email) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(lgpdService.exportData(organizationId, email));
    }

    @DeleteMapping("/forget")
    @Operation(summary = "Anonymize and soft-delete all personal data associated with an email (right to be forgotten)")
    public ResponseEntity<Void> deleteData(@RequestParam String email) {
        UUID organizationId = TenantContext.getOrganizationId();
        lgpdService.deleteData(organizationId, email);
        return ResponseEntity.noContent().build();
    }
}
