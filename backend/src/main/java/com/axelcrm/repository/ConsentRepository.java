package com.axelcrm.repository;

import com.axelcrm.entity.Consent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for LGPD {@link Consent} entities.
 */
public interface ConsentRepository extends JpaRepository<Consent, UUID> {

    Page<Consent> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    List<Consent> findByPersonEmailAndOrganization_IdAndDeletedAtIsNull(String email, UUID organizationId);

    Optional<Consent> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    long countByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
