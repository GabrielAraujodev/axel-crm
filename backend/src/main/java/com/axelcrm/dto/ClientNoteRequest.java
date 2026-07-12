package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientNoteRequest(
    @NotBlank(message = "Content is required")
    String content
) {
}
