package com.axelcrm.repository;

import com.axelcrm.entity.BankAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for BankAccount entities.
 */
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    Optional<BankAccount> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<BankAccount> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
