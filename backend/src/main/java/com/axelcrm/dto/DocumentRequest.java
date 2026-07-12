package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Document request payload")
public record DocumentRequest(

    @NotBlank
    @Schema(description = "Document name", example = "Contrato de Prestação de Serviços")
    String name,

    @Schema(description = "Optional description")
    String description,

    @Schema(description = "Category: CONTRACT, PROPOSAL, REPORT, NOTE, OTHER", example = "CONTRACT")
    String category,

    @Schema(description = "Comma-separated tags", example = "contrato,assinado,2025")
    String tags,

    @Schema(description = "Original file name")
    String fileName,

    @Schema(description = "MIME type", example = "application/pdf")
    String fileType,

    @Schema(description = "File size in bytes")
    Long fileSize,

    @Schema(description = "URL or storage path to the file")
    String fileUrl,

    @Schema(description = "Related client ID")
    UUID clientId,

    @Schema(description = "Related deal ID")
    UUID dealId,

    @Schema(description = "Related contract ID")
    UUID contractId,

    @Schema(description = "Related project ID")
    UUID projectId,

    @Schema(description = "Document date", example = "2025-01-15")
    LocalDate documentDate,

    @Schema(description = "Expiry date", example = "2026-01-15")
    LocalDate expiryDate,

    @Schema(description = "Whether the document is archived")
    boolean archived

) {
}
