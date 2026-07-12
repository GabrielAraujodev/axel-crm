package com.axelcrm.repository;

import com.axelcrm.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends JpaRepository<Partner, UUID> {
    Page<Partner> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
    Optional<Partner> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
