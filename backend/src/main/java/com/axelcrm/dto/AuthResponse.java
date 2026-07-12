package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.axelcrm.auth.dto.UserResponse;

/**
 * Authentication response containing the JWT token.
 */
@Schema(description = "Authentication response with JWT token")
public record AuthResponse(
        @Schema(description = "JWT access token")
        String accessToken,

        @Schema(description = "Token type", example = "Bearer")
        String tokenType,

        @Schema(description = "Token expiration time in seconds")
        Long expiresIn,

        @Schema(description = "Authenticated user summary")
        UserResponse user
) {
}
