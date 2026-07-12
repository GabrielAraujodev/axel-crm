package com.axelcrm.service;

import com.axelcrm.dto.CommissionRuleRequest;
import com.axelcrm.dto.CommissionRuleResponse;
import com.axelcrm.entity.CommissionRule;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.CommissionRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommissionRuleService {

    private final CommissionRuleRepository commissionRuleRepository;

    public Page<CommissionRuleResponse> findAll(UUID organizationId, Pageable pageable) {
        return commissionRuleRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public CommissionRuleResponse findById(UUID organizationId, UUID id) {
        return commissionRuleRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRule", "id", id));
    }

    @Transactional
    public CommissionRuleResponse create(UUID organizationId, CommissionRuleRequest request) {
        CommissionRule rule = new CommissionRule();
        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setPercentage(request.percentage());
        rule.setMinValue(request.minValue());
        rule.setMaxValue(request.maxValue());
        rule.setActive(request.active());

        rule = commissionRuleRepository.save(rule);
        return toResponse(rule);
    }

    @Transactional
    public CommissionRuleResponse update(UUID organizationId, UUID id, CommissionRuleRequest request) {
        CommissionRule rule = commissionRuleRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRule", "id", id));

        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setPercentage(request.percentage());
        rule.setMinValue(request.minValue());
        rule.setMaxValue(request.maxValue());
        rule.setActive(request.active());

        rule = commissionRuleRepository.save(rule);
        return toResponse(rule);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        CommissionRule rule = commissionRuleRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRule", "id", id));
        rule.setDeletedAt(java.time.LocalDateTime.now());
        commissionRuleRepository.save(rule);
    }

    private CommissionRuleResponse toResponse(CommissionRule rule) {
        return new CommissionRuleResponse(
                rule.getId(),
                rule.getName(),
                rule.getDescription(),
                rule.getPercentage(),
                rule.getMinValue(),
                rule.getMaxValue(),
                rule.isActive(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }
}
