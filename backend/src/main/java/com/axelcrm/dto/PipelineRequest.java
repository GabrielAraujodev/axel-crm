package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a Pipeline.
 */
public record PipelineRequest(
    @NotBlank @Size(max = 200) String name,
    @Size(max = 4000) String description,
    boolean active
) {
}
