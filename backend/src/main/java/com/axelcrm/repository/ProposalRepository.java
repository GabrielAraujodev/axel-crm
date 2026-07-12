package com.axelcrm.repository;

import com.axelcrm.entity.Proposal;
import com.axelcrm.entity.enums.ProposalStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Proposal} entities.
 */
public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    java.util.List<Proposal> findByOrganization_IdAndDeletedAtIsNull(UUID orgId);

    Page<Proposal> findByOrganization_IdAndDeletedAtIsNull(UUID orgId, Pageable pageable);

    long countByPartner_IdAndOrganization_IdAndDeletedAtIsNull(UUID partnerId, UUID orgId);

    long countByPartner_IdAndOrganization_IdAndStatusAndDeletedAtIsNull(UUID partnerId, UUID orgId, com.axelcrm.entity.enums.ProposalStatus status);

    Optional<Proposal> findByIdAndOrganization_Id(UUID id, UUID orgId);

    long countByStatusAndOrganization_Id(ProposalStatus status, UUID orgId);

    Page<Proposal> findByClient_IdAndClient_Organization_Id(UUID clientId, UUID orgId, Pageable pageable);

    Page<Proposal> findByAssignedTo_IdAndAssignedTo_Organization_Id(UUID userId, UUID orgId, Pageable pageable);

    long countByOrganization_IdAndDeletedAtIsNull(UUID orgId);

    long countByOrganization_IdAndCreatedAtBetweenAndDeletedAtIsNull(UUID orgId, LocalDateTime start, LocalDateTime end);

    Optional<Proposal> findByPublicTokenAndDeletedAtIsNull(UUID publicToken);

    java.util.List<Proposal> findByClient_IdAndStatusAndDeletedAtIsNullOrderByUpdatedAtDesc(UUID clientId, ProposalStatus status);
}
