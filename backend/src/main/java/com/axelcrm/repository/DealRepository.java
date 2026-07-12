package com.axelcrm.repository;

import com.axelcrm.entity.Deal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Deal} entities.
 * <p>
 * Provides organization-scoped queries for deals, including filtering by stage,
 * owner and expected close date range.
 */
public interface DealRepository extends JpaRepository<Deal, UUID>
{
	/**
	 * Returns all non-deleted deals that belong to the given organization.
	 *
	 * @param organizationId the organization id
	 * @param pageable       pagination info
	 * @return page of deals
	 */
	Page<Deal> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

	/**
	 * Returns all non-deleted deals that belong to the given organization.
	 *
	 * @param organizationId the organization id
	 * @return list of deals
	 */
	List<Deal> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId);

	/**
	 * Returns the deal with the given id and organization if not deleted.
	 *
	 * @param id             the deal id
	 * @param organizationId the organization id
	 * @return optional deal
	 */
	Optional<Deal> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

	Optional<Deal> findByIdAndOrganization_Id(UUID id, UUID organizationId);

	/**
	 * Returns deals in a specific pipeline stage.
	 *
	 * @param organizationId the organization id
	 * @param stageId        the deal stage id
	 * @return list of deals
	 */
	List<Deal> findByOrganization_IdAndStage_Id(UUID organizationId, UUID stageId);

	/**
	 * Returns deals assigned to a specific user.
	 *
	 * @param organizationId the organization id
	 * @param assignedToId   the assigned user id
	 * @return list of deals
	 */
	List<Deal> findByOrganization_IdAndAssignedTo_Id(UUID organizationId, UUID assignedToId);

	/**
	 * Returns deals whose expected close date falls within the given range.
	 *
	 * @param organizationId the organization id
	 * @param start          start of the date range (inclusive)
	 * @param end            end of the date range (inclusive)
	 * @return list of deals
	 */
	List<Deal> findByOrganization_IdAndExpectedCloseDateBetween(UUID organizationId, java.time.LocalDate start, java.time.LocalDate end);

	/**
	 * Returns deals with a value greater than or equal to the given amount.
	 *
	 * @param organizationId the organization id
	 * @param minValue       the minimum deal value
	 * @return list of deals
	 */
	List<Deal> findByOrganization_IdAndValueGreaterThanEqual(UUID organizationId, BigDecimal minValue);

	long countByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
