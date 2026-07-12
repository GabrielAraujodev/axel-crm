package com.axelcrm.repository;

import com.axelcrm.entity.ClientAttachment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAttachmentRepository extends JpaRepository<ClientAttachment, UUID> {
    List<ClientAttachment> findByOrganization_IdAndClient_IdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID organizationId, UUID clientId);
    Optional<ClientAttachment> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);
}
