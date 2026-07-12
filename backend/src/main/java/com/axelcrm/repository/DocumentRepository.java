package com.axelcrm.repository;

import com.axelcrm.entity.Document;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Optional<Document> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<Document> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
