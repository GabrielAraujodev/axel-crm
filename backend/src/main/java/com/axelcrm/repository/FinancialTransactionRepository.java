package com.axelcrm.repository;

import com.axelcrm.entity.FinancialTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for FinancialTransaction entities.
 */
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {

    Optional<FinancialTransaction> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<FinancialTransaction> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    java.util.List<FinancialTransaction> findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(
            UUID organizationId, java.time.LocalDate start, java.time.LocalDate end);

    java.util.List<FinancialTransaction> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
