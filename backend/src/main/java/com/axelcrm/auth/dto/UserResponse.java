package com.axelcrm.auth.dto;

import com.axelcrm.commons.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User response")
public record UserResponse(
        @Schema(description = "User ID") UUID id,
        @Schema(description = "Full name") String fullName,
        @Schema(description = "Email address") String email,
        @Schema(description = "User role") Role role,
        @Schema(description = "Active flag") Boolean active,
        @Schema(description = "Organization ID") UUID organizationId,
        @Schema(description = "Organization name") String organizationName,
        @Schema(description = "Last login") LocalDateTime lastLoginAt,
        @Schema(description = "Creation timestamp") LocalDateTime createdAt
) {
}
