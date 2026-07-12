package com.axelcrm.controller;

import com.axelcrm.dto.ClientAttachmentResponse;
import com.axelcrm.entity.ClientAttachment;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ClientAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/attachments")
@RequiredArgsConstructor
@Tag(name = "Client Attachments", description = "Endpoints for managing client file attachments")
public class ClientAttachmentController {

    private final ClientAttachmentService clientAttachmentService;

    @GetMapping
    @Operation(summary = "Get list of attachments for a client")
    public ResponseEntity<List<ClientAttachmentResponse>> findByClient(@PathVariable UUID clientId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(clientAttachmentService.findByClient(organizationId, clientId));
    }

    @PostMapping
    @Operation(summary = "Upload a new file attachment")
    public ResponseEntity<ClientAttachmentResponse> upload(
            @PathVariable UUID clientId,
            @RequestParam("file") MultipartFile file) throws IOException {
        UUID organizationId = TenantContext.getOrganizationId();
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(clientAttachmentService.create(organizationId, clientId, file, userId));
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download an attachment file")
    public ResponseEntity<byte[]> download(@PathVariable UUID clientId, @PathVariable UUID attachmentId) {
        UUID organizationId = TenantContext.getOrganizationId();
        ClientAttachment attachment = clientAttachmentService.findForDownload(organizationId, attachmentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, attachment.getFileType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(attachment.getFileSize()))
                .body(attachment.getFileData());
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<Void> delete(@PathVariable UUID clientId, @PathVariable UUID attachmentId) {
        UUID organizationId = TenantContext.getOrganizationId();
        clientAttachmentService.delete(organizationId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}
