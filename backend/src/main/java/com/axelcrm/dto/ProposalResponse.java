package com.axelcrm.dto;

import com.axelcrm.entity.enums.ProposalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.axelcrm.auth.dto.UserResponse;

/**
 * DTO for Proposal responses.
 */
public record ProposalResponse(
    UUID id,
    String proposalCode,
    UUID publicToken,
    String title,
    String description,
    ProposalStatus status,
    LocalDate issueDate,
    LocalDate validUntil,
    BigDecimal totalAmount,
    BigDecimal discountAmount,
    LocalDateTime approvedAt,
    ClientResponse client,
    UserResponse assignedTo,
    List<ProposalItemResponse> items,
    UserResponse captureUser,
    UserResponse sellerUser,
    PartnerResponse partner,
    UserResponse collaboratorUser,
    BigDecimal captureRate,
    BigDecimal sellerRate,
    BigDecimal partnerRate,
    BigDecimal collaboratorRate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public ProposalResponse(
        UUID id, String title, String description, ProposalStatus status,
        LocalDate issueDate, LocalDate validUntil, BigDecimal totalAmount,
        BigDecimal discountAmount, LocalDateTime approvedAt, ClientResponse client,
        UserResponse assignedTo, List<ProposalItemResponse> items,
        LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, null, null, title, description, status, issueDate, validUntil, totalAmount, discountAmount, approvedAt, client, assignedTo, items, null, null, null, null, null, null, null, null, createdAt, updatedAt);
    }
}
