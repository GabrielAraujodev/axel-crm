package com.axelcrm.service;

import com.axelcrm.dto.IntegrationRequest;
import com.axelcrm.dto.IntegrationResponse;
import com.axelcrm.entity.Integration;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.IntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationService {

    private final IntegrationRepository integrationRepository;

    public Page<IntegrationResponse> findAll(UUID organizationId, Pageable pageable) {
        return integrationRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public IntegrationResponse findById(UUID organizationId, UUID id) {
        return integrationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Integration", "id", id));
    }

    @Transactional
    public IntegrationResponse create(UUID organizationId, IntegrationRequest request) {
        Integration integration = new Integration();
        integration.setName(request.name());
        integration.setProvider(request.provider());
        integration.setCredentials(request.credentials());
        integration.setWebhookUrl(request.webhookUrl());
        integration.setApiKey(request.apiKey());
        integration.setActive(request.active());

        integration = integrationRepository.save(integration);
        return toResponse(integration);
    }

    @Transactional
    public IntegrationResponse update(UUID organizationId, UUID id, IntegrationRequest request) {
        Integration integration = integrationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Integration", "id", id));

        integration.setName(request.name());
        integration.setProvider(request.provider());
        integration.setCredentials(request.credentials());
        integration.setWebhookUrl(request.webhookUrl());
        integration.setApiKey(request.apiKey());
        integration.setActive(request.active());

        integration = integrationRepository.save(integration);
        return toResponse(integration);
    }

    @Transactional
    public IntegrationResponse sync(UUID organizationId, UUID id) {
        Integration integration = integrationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Integration", "id", id));

        integration.setLastSyncAt(LocalDateTime.now());
        integration = integrationRepository.save(integration);
        return toResponse(integration);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Integration integration = integrationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Integration", "id", id));
        integration.setDeletedAt(LocalDateTime.now());
        integrationRepository.save(integration);
    }

    private IntegrationResponse toResponse(Integration integration) {
        return new IntegrationResponse(
                integration.getId(),
                integration.getName(),
                integration.getProvider(),
                integration.getCredentials(),
                integration.getWebhookUrl(),
                integration.getApiKey(),
                integration.isActive(),
                integration.getLastSyncAt(),
                integration.getCreatedAt(),
                integration.getUpdatedAt()
        );
    }
}
