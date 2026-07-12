package com.axelcrm.dto;

import java.util.List;

/**
 * Combined response payload containing all personal data associated with an email for portability.
 */
public record LgpdDataExportResponse(
    String email,
    List<LeadResponse> leads,
    List<ClientResponse> clients,
    List<ContactResponse> contacts,
    List<ConsentResponse> consents
) {
}
