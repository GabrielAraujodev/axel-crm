package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import com.axelcrm.commons.entity.Organization;

/**
 * Organization response payload.
 */
@Schema(description = "Organization response")
public record OrganizationResponse(
        @Schema(description = "Organization ID")
        UUID id,

        @Schema(description = "Organization name")
        String name,

        @Schema(description = "Industry")
        String industry,

        @Schema(description = "Website")
        String website,

        @Schema(description = "Phone number")
        String phone,

        @Schema(description = "Address")
        String address,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt
) {
}
