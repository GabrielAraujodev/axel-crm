package com.axelcrm.service;

import com.axelcrm.dto.ContractRequest;
import com.axelcrm.dto.ContractResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contract;
import com.axelcrm.entity.Deal;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContractRepository;
import com.axelcrm.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;

    public Page<ContractResponse> findAll(UUID organizationId, Pageable pageable) {
        return contractRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public ContractResponse findById(UUID organizationId, UUID id) {
        return contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", id));
    }

    @Transactional
    public ContractResponse create(UUID organizationId, ContractRequest request) {
        Client client = clientRepository.findByIdAndOrganization_Id(
                request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        Contract contract = new Contract();
        contract.setTitle(request.title());
        contract.setContractNumber(request.contractNumber());
        contract.setDescription(request.description());
        contract.setClient(client);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setValue(request.value() != null ? request.value() : java.math.BigDecimal.ZERO);
        contract.setMonthlyValue(request.monthlyValue());
        contract.setStatus(request.status() != null ? request.status() : "DRAFT");
        contract.setTerms(request.terms());
        contract.setNotes(request.notes());
        contract.setSignedByClient(request.signedByClient());
        contract.setAutoRenew(request.autoRenew() != null && request.autoRenew());

        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.dealId(), organizationId)
                    .orElse(null);
            contract.setDeal(deal);
        }

        contract = contractRepository.save(contract);
        return toResponse(contract);
    }

    @Transactional
    public ContractResponse update(UUID organizationId, UUID id, ContractRequest request) {
        Contract contract = contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", id));

        Client client = clientRepository.findByIdAndOrganization_Id(
                request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        contract.setTitle(request.title());
        contract.setContractNumber(request.contractNumber());
        contract.setDescription(request.description());
        contract.setClient(client);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setValue(request.value() != null ? request.value() : java.math.BigDecimal.ZERO);
        contract.setMonthlyValue(request.monthlyValue());
        contract.setStatus(request.status() != null ? request.status() : "DRAFT");
        contract.setTerms(request.terms());
        contract.setNotes(request.notes());
        contract.setSignedByClient(request.signedByClient());
        contract.setAutoRenew(request.autoRenew() != null && request.autoRenew());

        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(
                    request.dealId(), organizationId)
                    .orElse(null);
            contract.setDeal(deal);
        } else {
            contract.setDeal(null);
        }

        contract = contractRepository.save(contract);
        return toResponse(contract);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Contract contract = contractRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract", "id", id));
        contract.setDeletedAt(LocalDateTime.now());
        contractRepository.save(contract);
    }

    private ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getTitle(),
                contract.getContractNumber(),
                contract.getDescription(),
                contract.getClient().getId(),
                contract.getClient().getName(),
                contract.getDeal() != null ? contract.getDeal().getId() : null,
                contract.getDeal() != null ? contract.getDeal().getTitle() : null,
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getValue(),
                contract.getMonthlyValue(),
                contract.getStatus(),
                contract.getTerms(),
                contract.getNotes(),
                contract.getSignedByClient(),
                contract.getSignedAt(),
                contract.getRenewedAt(),
                contract.isAutoRenew(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }
}
