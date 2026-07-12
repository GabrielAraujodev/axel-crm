package com.axelcrm.repository;

import com.axelcrm.entity.AuditLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>
{
	/**
	 * Returns all audit logs that belong to the given organization.
	 *
	 * @param organizationId the organization id
	 * @return list of audit logs
	 */
	List<AuditLog> findByOrganization_Id(UUID organizationId);

	/**
	 * Returns the audit log with the given id and organization.
	 *
	 * @param id             the audit log id
	 * @param organizationId the organization id
	 * @return optional audit log
	 */
	Optional<AuditLog> findByIdAndOrganization_Id(UUID id, UUID organizationId);

	/**
	 * Returns audit logs for a specific entity type within an organization.
	 *
	 * @param organizationId the organization id
	 * @param entityType     the entity type
	 * @return list of audit logs
	 */
	List<AuditLog> findByOrganization_IdAndEntityType(UUID organizationId, String entityType);

	/**
	 * Returns audit logs with the given action within an organization.
	 *
	 * @param organizationId the organization id
	 * @param action         the action name
	 * @return list of audit logs
	 */
	List<AuditLog> findByOrganization_IdAndAction(UUID organizationId, String action);

	/**
	 * Returns audit logs whose creation timestamp falls within the given range.
	 *
	 * @param organizationId the organization id
	 * @param start          start of the range (inclusive)
	 * @param end            end of the range (inclusive)
	 * @return list of audit logs
	 */
	List<AuditLog> findByOrganization_IdAndCreatedAtBetween(UUID organizationId, LocalDateTime start, LocalDateTime end);

	/**
	 * Returns audit logs for a specific entity type and entity ID, ordered by creation date descending.
	 *
	 * @param organizationId the organization id
	 * @param entityType     the entity type
	 * @param entityId       the entity ID as a string
	 * @return list of audit logs
	 */
	List<AuditLog> findByOrganization_IdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(UUID organizationId, String entityType, String entityId);
}

