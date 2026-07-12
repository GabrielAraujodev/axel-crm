package com.axelcrm.repository;

import com.axelcrm.entity.CalendarEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for CalendarEvent entities.
 */
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {

    Optional<CalendarEvent> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<CalendarEvent> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
