package com.axelcrm.repository;

import com.axelcrm.entity.Prospect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProspectRepository extends JpaRepository<Prospect, UUID> {
    Page<Prospect> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
    Optional<Prospect> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
