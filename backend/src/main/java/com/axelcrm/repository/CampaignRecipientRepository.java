package com.axelcrm.repository;

import com.axelcrm.entity.CampaignRecipient;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link CampaignRecipient} entities.
 */
public interface CampaignRecipientRepository extends JpaRepository<CampaignRecipient, UUID> {

    Page<CampaignRecipient> findByCampaign_IdAndOrganization_IdAndDeletedAtIsNull(
        UUID campaignId, UUID organizationId, Pageable pageable);

    List<CampaignRecipient> findByCampaign_IdAndOrganization_IdAndDeletedAtIsNull(
        UUID campaignId, UUID organizationId);
}
