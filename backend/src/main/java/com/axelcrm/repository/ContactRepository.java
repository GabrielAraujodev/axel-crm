package com.axelcrm.repository;

import com.axelcrm.entity.Contact;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Contact} entities.
 */
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    List<Contact> findByClient_IdAndClient_Organization_Id(UUID clientId, UUID orgId);

    Page<Contact> findByOrganization_IdAndDeletedAtIsNull(UUID orgId, Pageable pageable);

    Optional<Contact> findByIdAndOrganization_Id(UUID id, UUID orgId);

    java.util.List<Contact> findByEmailAndOrganization_Id(String email, UUID orgId);
}
