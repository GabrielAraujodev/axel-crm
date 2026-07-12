package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for PipelineStage responses.
 */
public record PipelineStageResponse(
    UUID id,
    UUID pipelineId,
    String name,
    Integer position,
    Integer winProbability,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
