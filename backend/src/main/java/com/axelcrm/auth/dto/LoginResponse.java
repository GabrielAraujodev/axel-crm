package com.axelcrm.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Login response with JWT token and user information")
public record LoginResponse(
    @Schema(description = "JWT access token") String token,
    @Schema(description = "Authenticated user ID") UUID userId,
    @Schema(description = "Authenticated user name") String userName,
    @Schema(description = "Email address of the authenticated user") String email,
    @Schema(description = "Role of the authenticated user") String role,
    @Schema(description = "Organization ID of the authenticated user") UUID organizationId,
    @Schema(description = "Organization name of the authenticated user") String organizationName
) {
}
