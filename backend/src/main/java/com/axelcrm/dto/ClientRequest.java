package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import com.axelcrm.entity.enums.ClientStatus;

/**
 * DTO for creating or updating a Client.
 * English: Client request payload.
 */
public record ClientRequest(
    @NotBlank @Size(max = 200) String name,
    @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 50) String taxId,
    @Size(max = 200) String companyName,
    @Size(max = 255) String website,
    @Size(max = 255) String industry,
    @Size(max = 500) String address,
    @Size(max = 100) String city,
    @Size(max = 100) String state,
    @Size(max = 50) String zipCode,
    @Size(max = 100) String country,
    @Size(max = 4000) String notes,
    Boolean active,
    ClientStatus status,
    String serviceType,
    UUID assignedToUserId
) {
    public ClientRequest(
        String name, String email, String phone, String taxId, String companyName,
        String website, String industry, String address, String city, String state,
        String zipCode, String country, String notes, Boolean active,
        ClientStatus status, UUID assignedToUserId
    ) {
        this(name, email, phone, taxId, companyName, website, industry, address, city, state, zipCode, country, notes, active, status, null, assignedToUserId);
    }
}
