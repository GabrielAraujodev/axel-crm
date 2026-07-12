package com.axelcrm.repository;

import com.axelcrm.entity.Invoice;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<Invoice> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);

    List<Invoice> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId);
}
