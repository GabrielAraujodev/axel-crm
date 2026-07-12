package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import com.axelcrm.commons.entity.Organization;

/**
 * Request payload for creating/updating an organization.
 */
@Schema(description = "Organization request")
public record OrganizationRequest(
        @NotBlank
        @Schema(description = "Organization name", example = "Acme Corp")
        String name,

        @Schema(description = "Industry", example = "Technology")
        String industry,

        @Schema(description = "Website", example = "https://acme.com")
        String website,

        @Schema(description = "Phone number", example = "+1 555 1234")
        String phone,

        @Schema(description = "Address")
        String address
) {
}
