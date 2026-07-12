package com.axelcrm.repository;

import com.axelcrm.entity.TimeEntry;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for TimeEntry entities.
 */
public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {

    Optional<TimeEntry> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<TimeEntry> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
