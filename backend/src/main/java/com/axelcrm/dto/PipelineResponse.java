package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Pipeline responses.
 */
public record PipelineResponse(
    UUID id,
    String name,
    String description,
    boolean active,
    List<PipelineStageResponse> stages,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
