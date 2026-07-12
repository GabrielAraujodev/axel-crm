package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for creating or updating a PipelineStage.
 */
public record PipelineStageRequest(
    @NotNull UUID pipelineId,
    @NotBlank @Size(max = 200) String name,
    @NotNull Integer position,
    Integer winProbability,
    @Size(max = 4000) String description
) {
}
