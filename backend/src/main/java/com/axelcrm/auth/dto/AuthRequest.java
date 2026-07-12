package com.axelcrm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User authentication request")
public record AuthRequest(
        @NotBlank
        @Schema(description = "Username or email", example = "admin@axelcrm.com")
        String username,

        @NotBlank
        @Schema(description = "Password", example = "admin123")
        String password
) {
}
