package com.axelcrm.dto;

import com.axelcrm.entity.enums.ContactType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Contact responses.
 * English: Contact response payload.
 */
public record ContactResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String jobTitle,
    String department,
    String notes,
    ContactType contactType,
    ClientResponse client,
    LeadResponse lead,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public ContactResponse(
        UUID id, String firstName, String lastName, String email, String phone,
        String jobTitle, String department, String notes, ClientResponse client,
        LeadResponse lead, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, firstName, lastName, email, phone, jobTitle, department, notes, null, client, lead, createdAt, updatedAt);
    }
}
