package com.axelcrm.service;

import com.axelcrm.dto.AuditLogRequest;
import com.axelcrm.dto.AuditLogResponse;
import com.axelcrm.entity.AuditLog;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.AuditLogRepository;
import com.axelcrm.auth.repository.OrganizationRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<AuditLogResponse> findAll(UUID organizationId) {
        return auditLogRepository.findByOrganization_Id(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuditLogResponse> findByEntityType(UUID organizationId, String entityType) {
        return auditLogRepository.findByOrganization_IdAndEntityType(organizationId, entityType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuditLogResponse> findByAction(UUID organizationId, String action) {
        return auditLogRepository.findByOrganization_IdAndAction(organizationId, action)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuditLogResponse> findByDateRange(UUID organizationId, LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByOrganization_IdAndCreatedAtBetween(organizationId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AuditLogResponse create(AuditLogRequest request) {
        Organization org = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", request.organizationId()));

        AuditLog log = new AuditLog();
        log.setOrganization(org);
        log.setAction(request.action());
        log.setEntityType(request.entityType());
        log.setEntityId(request.entityId());
        log.setOldValues(request.oldValues());
        log.setNewValues(request.newValues());

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));
            log.setUser(user);
        }

        log = auditLogRepository.save(log);
        return toResponse(log);
    }

    @Transactional
    public void log(UUID organizationId, UUID userId, String action, String entityType, String entityId, String oldValues, String newValues) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", organizationId));

        AuditLog log = new AuditLog();
        log.setOrganization(org);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);

        if (userId != null) {
            userRepository.findById(userId).ifPresent(log::setUser);
        }

        auditLogRepository.save(log);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getOrganization() != null ? log.getOrganization().getId() : null,
                log.getUser() != null ? log.getUser().getId() : null,
                log.getUser() != null ? log.getUser().getName() : null,
                log.getEntityType(),
                log.getEntityId(),
                log.getAction(),
                log.getOldValues(),
                log.getNewValues(),
                log.getCreatedAt()
        );
    }
}
