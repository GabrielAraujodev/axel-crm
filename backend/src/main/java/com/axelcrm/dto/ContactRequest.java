package com.axelcrm.dto;

import com.axelcrm.entity.enums.ContactType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for creating or updating a Contact.
 * English: Contact request payload.
 */
public record ContactRequest(
    @NotBlank @Size(max = 200) String firstName,
    @Size(max = 200) String lastName,
    @Email @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 100) String jobTitle,
    @Size(max = 255) String department,
    @Size(max = 4000) String notes,
    ContactType contactType,
    UUID clientId,
    UUID leadId
) {
    public ContactRequest(
        String firstName, String lastName, String email, String phone, String jobTitle,
        String department, String notes, UUID clientId, UUID leadId
    ) {
        this(firstName, lastName, email, phone, jobTitle, department, notes, null, clientId, leadId);
    }
}
