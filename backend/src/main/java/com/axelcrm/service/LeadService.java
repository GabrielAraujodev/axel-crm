package com.axelcrm.service;

import com.axelcrm.dto.LeadRequest;
import com.axelcrm.dto.LeadResponse;
import com.axelcrm.auth.dto.UserResponse;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.entity.Lead;
import com.axelcrm.auth.entity.User;
import com.axelcrm.entity.enums.LeadStage;
import com.axelcrm.repository.LeadRepository;
import com.axelcrm.repository.PartnerRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    public Page<LeadResponse> findAll(UUID organizationId, Pageable pageable) {
        return leadRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public LeadResponse findById(UUID organizationId, UUID id) {
        return leadRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
    }

    @Transactional
    public LeadResponse create(UUID organizationId, LeadRequest request) {
        Lead lead = new Lead();
        lead.setName(request.name());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setCompany(request.companyName());
        lead.setPosition(request.jobTitle());
        lead.setSource(request.source());
        lead.setStage(request.stage() != null ? request.stage() : LeadStage.NEW);
        lead.setEstimatedValue(request.estimatedValue() != null ? request.estimatedValue() : BigDecimal.ZERO);
        lead.setNotes(request.notes());

        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            lead.setAssignedTo(assigned);
        }
        
        if (request.partnerId() != null) {
            lead.setPartner(partnerRepository.findByIdAndOrganization_Id(request.partnerId(), organizationId).orElse(null));
        }

        lead = leadRepository.save(lead);
        return toResponse(lead);
    }

    @Transactional
    public LeadResponse update(UUID organizationId, UUID id, LeadRequest request) {
        Lead lead = leadRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        lead.setName(request.name());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setCompany(request.companyName());
        lead.setPosition(request.jobTitle());
        lead.setSource(request.source());
        lead.setStage(request.stage());
        lead.setEstimatedValue(request.estimatedValue());
        lead.setNotes(request.notes());

        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            lead.setAssignedTo(assigned);
        } else {
            lead.setAssignedTo(null);
        }

        if (request.partnerId() != null) {
            lead.setPartner(partnerRepository.findByIdAndOrganization_Id(request.partnerId(), organizationId).orElse(null));
        } else {
            lead.setPartner(null);
        }

        lead = leadRepository.save(lead);
        return toResponse(lead);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Lead lead = leadRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        lead.setDeletedAt(java.time.LocalDateTime.now());
        leadRepository.save(lead);
    }

    private LeadResponse toResponse(Lead lead) {
        UserResponse assignedTo = lead.getAssignedTo() != null
                ? new UserResponse(lead.getAssignedTo().getId(), lead.getAssignedTo().getName(),
                lead.getAssignedTo().getEmail(), lead.getAssignedTo().getRole(),
                lead.getAssignedTo().isActive(), null, null, null, null)
                : null;

        return new LeadResponse(
                lead.getId(), lead.getName(), lead.getEmail(), lead.getPhone(),
                lead.getCompany(), lead.getPosition(), lead.getSource(), lead.getStage(),
                lead.getEstimatedValue(), lead.getNotes(), lead.getScore(), lead.getConvertedAt(),
                assignedTo, lead.getCreatedAt(), lead.getUpdatedAt()
        );
    }
}
