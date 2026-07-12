package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import com.axelcrm.entity.enums.ClientStatus;

/**
 * DTO for Client responses.
 * English: Client response payload.
 */
public record ClientResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String taxId,
    String companyName,
    String website,
    String industry,
    String address,
    String city,
    String state,
    String zipCode,
    String country,
    String notes,
    Boolean active,
    ClientStatus status,
    String serviceType,
    UUID assignedToUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public ClientResponse(
        UUID id, String name, String email, String phone, String taxId, String companyName,
        String website, String industry, String address, String city, String state,
        String zipCode, String country, String notes, Boolean active, ClientStatus status,
        UUID assignedToUserId, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, name, email, phone, taxId, companyName, website, industry, address, city, state, zipCode, country, notes, active, status, null, assignedToUserId, createdAt, updatedAt);
    }
}
