package com.axelcrm.service;

import com.axelcrm.dto.ProspectRequest;
import com.axelcrm.dto.ProspectResponse;
import com.axelcrm.entity.Lead;
import com.axelcrm.entity.Prospect;
import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.LeadStage;
import com.axelcrm.entity.enums.ProspectStage;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LeadRepository;
import com.axelcrm.repository.ProspectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProspectService {

    private final ProspectRepository prospectRepository;
    private final LeadRepository leadRepository;

    public Page<ProspectResponse> findAll(UUID organizationId, Pageable pageable) {
        return prospectRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public ProspectResponse findById(UUID organizationId, UUID id) {
        return prospectRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "id", id));
    }

    @Transactional
    public ProspectResponse create(UUID organizationId, ProspectRequest request) {
        Prospect prospect = new Prospect();
        prospect.setName(request.name());
        prospect.setEmail(request.email());
        prospect.setPhone(request.phone());
        prospect.setCompany(request.company());
        prospect.setSource(request.source() != null ? request.source() : LeadSource.OTHER);
        prospect.setStage(request.stage() != null ? request.stage() : ProspectStage.PROSPECTING);
        prospect.setNotes(request.notes());

        prospect = prospectRepository.save(prospect);
        return toResponse(prospect);
    }

    @Transactional
    public ProspectResponse update(UUID organizationId, UUID id, ProspectRequest request) {
        Prospect prospect = prospectRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "id", id));

        prospect.setName(request.name());
        prospect.setEmail(request.email());
        prospect.setPhone(request.phone());
        prospect.setCompany(request.company());
        if (request.source() != null) prospect.setSource(request.source());
        if (request.stage() != null) prospect.setStage(request.stage());
        prospect.setNotes(request.notes());

        prospect = prospectRepository.save(prospect);
        return toResponse(prospect);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Prospect prospect = prospectRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "id", id));
        prospect.setDeletedAt(LocalDateTime.now());
        prospectRepository.save(prospect);
    }

    @Transactional
    public ProspectResponse promoteToLead(UUID organizationId, UUID id) {
        Prospect prospect = prospectRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Prospect", "id", id));

        if (prospect.getConvertedLead() != null) {
            throw new IllegalStateException("Prospect is already converted to Lead.");
        }

        Lead lead = new Lead();
        lead.setName(prospect.getName());
        lead.setEmail(prospect.getEmail());
        lead.setPhone(prospect.getPhone());
        lead.setCompany(prospect.getCompany());
        lead.setSource(prospect.getSource());
        lead.setStage(LeadStage.NEW);
        lead.setNotes(prospect.getNotes());
        
        // Save the lead
        lead = leadRepository.save(lead);

        // Update the prospect
        prospect.setConvertedLead(lead);
        prospect.setConvertedAt(LocalDateTime.now());
        prospect = prospectRepository.save(prospect);

        return toResponse(prospect);
    }

    private ProspectResponse toResponse(Prospect prospect) {
        return new ProspectResponse(
                prospect.getId(),
                prospect.getName(),
                prospect.getEmail(),
                prospect.getPhone(),
                prospect.getCompany(),
                prospect.getSource(),
                prospect.getStage(),
                prospect.getNotes(),
                prospect.getConvertedLead() != null ? prospect.getConvertedLead().getId() : null,
                prospect.getConvertedAt(),
                prospect.getCreatedAt(),
                prospect.getUpdatedAt()
        );
    }
}
