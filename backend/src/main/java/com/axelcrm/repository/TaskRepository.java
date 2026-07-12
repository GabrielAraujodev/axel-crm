package com.axelcrm.repository;

import com.axelcrm.entity.Task;
import com.axelcrm.entity.enums.TaskStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link Task} entities.
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByOrganization_IdAndDeletedAtIsNull(UUID orgId, Pageable pageable);

    Optional<Task> findByIdAndOrganization_Id(UUID id, UUID orgId);

    Page<Task> findByAssignedTo_IdAndAssignedTo_Organization_Id(UUID userId, UUID orgId, Pageable pageable);

    long countByStatusAndOrganization_Id(TaskStatus status, UUID orgId);

    @Query("SELECT t FROM Task t WHERE t.organization.id = :orgId AND t.deletedAt IS NULL AND t.dueDate BETWEEN :start AND :end")
    Page<Task> findByDueDateBetweenAndOrganization_Id(
        @Param("orgId") UUID orgId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    long countByOrganization_IdAndDeletedAtIsNull(UUID orgId);
}
