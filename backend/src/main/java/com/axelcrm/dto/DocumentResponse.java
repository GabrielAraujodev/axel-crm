package com.axelcrm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
    UUID id,
    String name,
    String description,
    String category,
    String tags,
    String fileName,
    String fileType,
    Long fileSize,
    String fileUrl,
    UUID clientId,
    String clientName,
    UUID dealId,
    String dealTitle,
    UUID contractId,
    String contractTitle,
    UUID projectId,
    String projectName,
    LocalDate documentDate,
    LocalDate expiryDate,
    boolean archived,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
