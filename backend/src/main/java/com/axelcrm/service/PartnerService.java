package com.axelcrm.service;

import com.axelcrm.dto.PartnerRequest;
import com.axelcrm.dto.PartnerResponse;
import com.axelcrm.entity.Partner;
import com.axelcrm.entity.enums.ProposalStatus;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LeadRepository;
import com.axelcrm.repository.PartnerRepository;
import com.axelcrm.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final LeadRepository leadRepository;
    private final ProposalRepository proposalRepository;

    public Page<PartnerResponse> findAll(UUID organizationId, Pageable pageable) {
        return partnerRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponseWithKpis);
    }

    public PartnerResponse findById(UUID organizationId, UUID id) {
        return partnerRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponseWithKpis)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
    }

    @Transactional
    public PartnerResponse create(UUID organizationId, PartnerRequest request) {
        Partner partner = new Partner();
        partner.setName(request.name());
        partner.setEmail(request.email());
        partner.setPhone(request.phone());
        partner.setCompany(request.company());
        partner.setBankDetails(request.bankDetails());
        partner.setCommissionPercentage(request.commissionPercentage() != null ? request.commissionPercentage() : BigDecimal.ZERO);
        
        partner = partnerRepository.save(partner);
        return toResponseWithKpis(partner);
    }

    @Transactional
    public PartnerResponse update(UUID organizationId, UUID id, PartnerRequest request) {
        Partner partner = partnerRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));

        partner.setName(request.name());
        partner.setEmail(request.email());
        partner.setPhone(request.phone());
        partner.setCompany(request.company());
        partner.setBankDetails(request.bankDetails());
        if (request.commissionPercentage() != null) {
            partner.setCommissionPercentage(request.commissionPercentage());
        }

        partner = partnerRepository.save(partner);
        return toResponseWithKpis(partner);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Partner partner = partnerRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
        partner.setDeletedAt(LocalDateTime.now());
        partnerRepository.save(partner);
    }

    private PartnerResponse toResponseWithKpis(Partner partner) {
        long totalReferrals = leadRepository.countByPartner_IdAndOrganization_IdAndDeletedAtIsNull(partner.getId(), partner.getOrganization().getId());
        long proposalsSent = proposalRepository.countByPartner_IdAndOrganization_IdAndDeletedAtIsNull(partner.getId(), partner.getOrganization().getId());
        long closedProposals = proposalRepository.countByPartner_IdAndOrganization_IdAndStatusAndDeletedAtIsNull(partner.getId(), partner.getOrganization().getId(), ProposalStatus.ACCEPTED);
        
        BigDecimal conversionRate = BigDecimal.ZERO;
        if (totalReferrals > 0) {
            conversionRate = BigDecimal.valueOf(closedProposals).divide(BigDecimal.valueOf(totalReferrals), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        return new PartnerResponse(
            partner.getId(),
            partner.getName(),
            partner.getEmail(),
            partner.getPhone(),
            partner.getCompany(),
            partner.getBankDetails(),
            partner.getCommissionPercentage(),
            partner.getCreatedAt(),
            partner.getUpdatedAt(),
            totalReferrals,
            proposalsSent,
            conversionRate
        );
    }
}
