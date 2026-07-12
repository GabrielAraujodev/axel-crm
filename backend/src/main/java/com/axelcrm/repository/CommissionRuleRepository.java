package com.axelcrm.repository;

import com.axelcrm.entity.CommissionRule;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for CommissionRule entities.
 */
public interface CommissionRuleRepository extends JpaRepository<CommissionRule, UUID> {

    Optional<CommissionRule> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<CommissionRule> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
