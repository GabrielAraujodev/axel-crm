package com.axelcrm.auth.repository;

import com.axelcrm.commons.entity.Organization;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID>
{
	Optional<Organization> findByDomain(String domain);
	boolean existsByDomain(String domain);
}
