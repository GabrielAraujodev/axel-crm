package com.axelcrm.dto;

import com.axelcrm.entity.enums.ProposalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating or updating a Proposal.
 */
public record ProposalRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 4000) String description,
    ProposalStatus status,
    LocalDate issueDate,
    LocalDate validUntil,
    BigDecimal discountAmount,
    @NotNull UUID clientId,
    UUID assignedToUserId,
    UUID partnerId,
    List<ProposalItemRequest> items,
    UUID captureUserId,
    UUID sellerUserId,
    UUID collaboratorUserId,
    BigDecimal captureRate,
    BigDecimal sellerRate,
    BigDecimal partnerRate,
    BigDecimal collaboratorRate,
    UUID dealId
) {
    public ProposalRequest(
        String title, String description, ProposalStatus status, LocalDate issueDate,
        LocalDate validUntil, BigDecimal discountAmount, UUID clientId,
        UUID assignedToUserId, UUID partnerId, List<ProposalItemRequest> items
    ) {
        this(title, description, status, issueDate, validUntil, discountAmount, clientId, assignedToUserId, partnerId, items, null, null, null, null, null, null, null, null);
    }
}
