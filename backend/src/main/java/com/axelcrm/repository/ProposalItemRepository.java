package com.axelcrm.repository;

import com.axelcrm.entity.ProposalItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link ProposalItem} entities.
 */
public interface ProposalItemRepository extends JpaRepository<ProposalItem, UUID> {

    List<ProposalItem> findByProposal_IdAndProposal_Organization_Id(UUID proposalId, UUID orgId);

    Page<ProposalItem> findByOrganization_IdAndDeletedAtIsNull(UUID orgId, Pageable pageable);

    Optional<ProposalItem> findByIdAndOrganization_Id(UUID id, UUID orgId);
}
