package com.axelcrm.repository;

import com.axelcrm.entity.SupportTicket;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for SupportTicket entities.
 */
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    Optional<SupportTicket> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<SupportTicket> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    long countByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
