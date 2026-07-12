package com.axelcrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for registering or updating an LGPD consent.
 */
public record ConsentRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String personEmail,

    @NotBlank(message = "Consent type is required")
    String consentType,

    @NotNull(message = "Granted status is required")
    Boolean granted,

    String ipAddress,
    String userAgent
) {
}
