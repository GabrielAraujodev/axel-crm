package com.axelcrm.service;

import com.axelcrm.dto.CommissionRequest;
import com.axelcrm.dto.CommissionResponse;
import com.axelcrm.entity.Commission;
import com.axelcrm.entity.CommissionRule;
import com.axelcrm.entity.Deal;
import com.axelcrm.auth.entity.User;
import com.axelcrm.entity.Partner;
import com.axelcrm.entity.Proposal;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.CommissionRepository;
import com.axelcrm.repository.CommissionRuleRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.repository.ProposalRepository;
import com.axelcrm.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CommissionService {

    private final CommissionRepository commissionRepository;
    private final UserRepository userRepository;
    private final DealRepository dealRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final ProposalRepository proposalRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    public Page<CommissionResponse> findAll(UUID organizationId, Pageable pageable) {
        return commissionRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public CommissionResponse findById(UUID organizationId, UUID id) {
        return commissionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", "id", id));
    }

    @Transactional
    public CommissionResponse create(UUID organizationId, CommissionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));

        CommissionRule rule = commissionRuleRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.ruleId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRule", "id", request.ruleId()));

        Commission commission = new Commission();
        commission.setUser(user);
        commission.setDeal(deal);
        commission.setRule(rule);
        commission.setDealValue(request.dealValue());
        
        java.math.BigDecimal amount = request.amount();
        if (amount == null) {
            amount = request.dealValue().multiply(rule.getPercentage());
        }
        commission.setAmount(amount);
        commission.setPaid(false);

        commission = commissionRepository.save(commission);
        return toResponse(commission);
    }

    @Transactional
    public CommissionResponse update(UUID organizationId, UUID id, CommissionRequest request) {
        Commission commission = commissionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", "id", id));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));

        CommissionRule rule = commissionRuleRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.ruleId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionRule", "id", request.ruleId()));

        commission.setUser(user);
        commission.setDeal(deal);
        commission.setRule(rule);
        commission.setDealValue(request.dealValue());

        java.math.BigDecimal amount = request.amount();
        if (amount == null) {
            amount = request.dealValue().multiply(rule.getPercentage());
        }
        commission.setAmount(amount);

        commission = commissionRepository.save(commission);
        return toResponse(commission);
    }

    @Transactional
    public CommissionResponse payCommission(UUID organizationId, UUID id) {
        Commission commission = commissionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", "id", id));
        commission.setPaid(true);
        commission.setPaidAt(java.time.LocalDateTime.now());
        commission = commissionRepository.save(commission);

        // Auto create debit transaction
        FinancialTransaction tx = new FinancialTransaction();
        tx.setOrganization(commission.getOrganization());
        String recipientName = commission.getUser() != null ? commission.getUser().getName() : 
                               (commission.getPartner() != null ? commission.getPartner().getName() : "Desconhecido");
        tx.setDescription("Pagamento de comissão: " + (commission.getDeal() != null ? commission.getDeal().getTitle() : "") + " (" + recipientName + ")");
        tx.setTransactionType(com.axelcrm.entity.enums.TransactionType.EXPENSE);
        tx.setAmount(commission.getAmount());
        tx.setTransactionDate(java.time.LocalDate.now());
        tx.setPaid(true);
        tx.setPaidAt(java.time.LocalDateTime.now());
        tx.setCategory("Comissão");

        financialTransactionRepository.save(tx);

        return toResponse(commission);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Commission commission = commissionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission", "id", id));
        commission.setDeletedAt(java.time.LocalDateTime.now());
        commissionRepository.save(commission);
    }

    private CommissionResponse toResponse(Commission commission) {
        return new CommissionResponse(
                commission.getId(),
                commission.getUser() != null ? commission.getUser().getId() : null,
                commission.getUser() != null ? commission.getUser().getName() : null,
                commission.getPartner() != null ? commission.getPartner().getId() : null,
                commission.getPartner() != null ? commission.getPartner().getName() : null,
                commission.getRole(),
                commission.getAvailableAt(),
                commission.getDeal() != null ? commission.getDeal().getId() : null,
                commission.getDeal() != null ? commission.getDeal().getTitle() : null,
                commission.getRule() != null ? commission.getRule().getId() : null,
                commission.getRule() != null ? commission.getRule().getName() : null,
                commission.getDealValue(),
                commission.getAmount(),
                commission.getPaidAt(),
                commission.isPaid(),
                commission.getCreatedAt(),
                commission.getUpdatedAt()
        );
    }

    @Transactional
    public void calculateCommissionsForTransaction(FinancialTransaction tx) {
        if (tx.getTransactionType() != com.axelcrm.entity.enums.TransactionType.INCOME || !tx.isPaid() || tx.getDeal() == null) {
            return;
        }

        Deal deal = tx.getDeal();
        UUID organizationId = tx.getOrganization().getId();

        // Find the accepted proposal for this client
        List<Proposal> proposals = proposalRepository.findByClient_IdAndStatusAndDeletedAtIsNullOrderByUpdatedAtDesc(deal.getClient().getId(), com.axelcrm.entity.enums.ProposalStatus.ACCEPTED);
        if (proposals.isEmpty()) {
            return; // No approved proposal to extract multi-level commission rules from
        }
        Proposal proposal = proposals.get(0);

        java.math.BigDecimal txAmount = tx.getAmount();
        java.time.LocalDate paymentDate = tx.getTransactionDate();
        if (paymentDate == null) {
            paymentDate = java.time.LocalDate.now();
        }
        java.time.LocalDate releaseDate = paymentDate.plusDays(30);

        // 1. Captação (Capture)
        if (proposal.getCaptureUser() != null && proposal.getCaptureRate() != null && proposal.getCaptureRate().compareTo(java.math.BigDecimal.ZERO) > 0) {
            createMultiLevelCommission(organizationId, deal, proposal.getCaptureUser(), null, "CAPTURE", txAmount, proposal.getCaptureRate(), releaseDate);
        }

        // 2. Vendas (Seller)
        if (proposal.getSellerUser() != null && proposal.getSellerRate() != null && proposal.getSellerRate().compareTo(java.math.BigDecimal.ZERO) > 0) {
            createMultiLevelCommission(organizationId, deal, proposal.getSellerUser(), null, "SELLER", txAmount, proposal.getSellerRate(), releaseDate);
        }

        // 3. Parceiro (Partner)
        if (proposal.getPartner() != null && proposal.getPartnerRate() != null && proposal.getPartnerRate().compareTo(java.math.BigDecimal.ZERO) > 0) {
            createMultiLevelCommission(organizationId, deal, null, proposal.getPartner(), "PARTNER", txAmount, proposal.getPartnerRate(), releaseDate);
        }

        // 4. Colaborador (Collaborator)
        if (proposal.getCollaboratorUser() != null && proposal.getCollaboratorRate() != null && proposal.getCollaboratorRate().compareTo(java.math.BigDecimal.ZERO) > 0) {
            createMultiLevelCommission(organizationId, deal, proposal.getCollaboratorUser(), null, "COLLABORATOR", txAmount, proposal.getCollaboratorRate(), releaseDate);
        }
    }

    private void createMultiLevelCommission(UUID organizationId, Deal deal, User user, Partner partner, String role, java.math.BigDecimal txAmount, java.math.BigDecimal rate, java.time.LocalDate releaseDate) {
        boolean exists;
        if (user != null) {
            exists = commissionRepository.existsByDeal_IdAndUser_IdAndRoleAndDeletedAtIsNull(deal.getId(), user.getId(), role);
        } else {
            exists = commissionRepository.existsByDeal_IdAndPartner_IdAndRoleAndDeletedAtIsNull(deal.getId(), partner.getId(), role);
        }

        if (exists) {
            return;
        }

        Commission comm = new Commission();
        comm.setOrganization(deal.getOrganization());
        comm.setDeal(deal);
        comm.setUser(user);
        comm.setPartner(partner);
        comm.setRole(role);
        comm.setDealValue(txAmount);
        comm.setAmount(txAmount.multiply(rate));
        comm.setAvailableAt(releaseDate);
        comm.setPaid(false);

        commissionRepository.save(comm);
    }
}
