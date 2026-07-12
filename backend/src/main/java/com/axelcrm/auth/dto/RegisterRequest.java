package com.axelcrm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request for a new organization")
public record RegisterRequest(
        @NotBlank
        @Schema(description = "Organization name", example = "Acme Corp")
        String organizationName,

        @NotBlank
        @Schema(description = "Full name", example = "John Doe")
        String fullName,

        @NotBlank
        @Email
        @Schema(description = "Email address", example = "john@acme.com")
        String email,

        @NotBlank
        @Size(min = 6)
        @Schema(description = "Password", example = "securePass123")
        String password
) {
}
