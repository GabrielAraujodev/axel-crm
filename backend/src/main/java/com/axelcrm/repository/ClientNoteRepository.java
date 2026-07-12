package com.axelcrm.repository;

import com.axelcrm.entity.ClientNote;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientNoteRepository extends JpaRepository<ClientNote, UUID> {
    List<ClientNote> findByOrganization_IdAndClient_IdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID organizationId, UUID clientId);
}
