package com.axelcrm.auth.dto;

import com.axelcrm.commons.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User request")
public record UserRequest(
        @NotBlank
        @Schema(description = "Full name", example = "John Doe")
        String fullName,

        @NotBlank
        @Email
        @Schema(description = "Email address", example = "john@acme.com")
        String email,

        @Schema(description = "Password (required for creation)")
        String password,

        @Schema(description = "User role", example = "USER")
        Role role,

        @Schema(description = "Active flag", example = "true")
        Boolean active
) {
}
