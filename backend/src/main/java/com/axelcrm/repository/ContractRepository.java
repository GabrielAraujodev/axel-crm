package com.axelcrm.repository;

import com.axelcrm.entity.Contract;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    Optional<Contract> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<Contract> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
