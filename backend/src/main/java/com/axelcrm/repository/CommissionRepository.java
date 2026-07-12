package com.axelcrm.repository;

import com.axelcrm.entity.Commission;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Commission entities.
 */
public interface CommissionRepository extends JpaRepository<Commission, UUID> {

    Optional<Commission> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<Commission> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    boolean existsByDeal_IdAndUser_IdAndRoleAndDeletedAtIsNull(UUID dealId, UUID userId, String role);

    boolean existsByDeal_IdAndPartner_IdAndRoleAndDeletedAtIsNull(UUID dealId, UUID partnerId, String role);
}
