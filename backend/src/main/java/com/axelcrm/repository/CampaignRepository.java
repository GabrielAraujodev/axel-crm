package com.axelcrm.repository;

import com.axelcrm.entity.Campaign;
import com.axelcrm.entity.enums.CampaignType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Campaign} entities.
 * <p>
 * Provides organization-scoped queries for marketing campaigns, including
 * filtering by type and status.
 */
public interface CampaignRepository extends JpaRepository<Campaign, UUID>
{
	/**
	 * Returns all campaigns that belong to the given organization.
	 *
	 * @param organizationId the organization id
	 * @return list of campaigns
	 */
	List<Campaign> findByOrganization_Id(UUID organizationId);

	/**
	 * Returns the campaign with the given id and organization.
	 *
	 * @param id             the campaign id
	 * @param organizationId the organization id
	 * @return optional campaign
	 */
	Optional<Campaign> findByIdAndOrganization_Id(UUID id, UUID organizationId);

	/**
	 * Returns campaigns of a specific type within an organization.
	 *
	 * @param organizationId the organization id
	 * @param type           the campaign type
	 * @return list of campaigns
	 */
	List<Campaign> findByOrganization_IdAndType(UUID organizationId, CampaignType type);

	/**
	 * Returns campaigns with the given status within an organization.
	 *
	 * @param organizationId the organization id
	 * @param status         the campaign status
	 * @return list of campaigns
	 */
	List<Campaign> findByOrganization_IdAndStatus(UUID organizationId, String status);

	long countByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
