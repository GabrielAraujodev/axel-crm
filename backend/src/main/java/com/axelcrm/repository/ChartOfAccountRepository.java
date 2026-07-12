package com.axelcrm.repository;

import com.axelcrm.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, UUID> {

    List<ChartOfAccount> findByOrganization_IdAndDeletedAtIsNullOrderByCodeAsc(UUID organizationId);

    List<ChartOfAccount> findByOrganization_IdAndParentIsNullAndDeletedAtIsNullOrderByCodeAsc(UUID organizationId);

    Optional<ChartOfAccount> findByOrganization_IdAndCodeAndDeletedAtIsNull(UUID organizationId, String code);

    Optional<ChartOfAccount> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);
}
