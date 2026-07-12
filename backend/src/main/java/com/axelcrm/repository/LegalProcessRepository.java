package com.axelcrm.repository;

import com.axelcrm.entity.LegalProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LegalProcessRepository extends JpaRepository<LegalProcess, UUID> {

    Optional<LegalProcess> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<LegalProcess> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    Optional<LegalProcess> findByOrganization_IdAndCnjNumberAndDeletedAtIsNull(UUID organizationId, String cnjNumber);
}
