package com.axelcrm.service;

import com.axelcrm.dto.FinancialTransactionRequest;
import com.axelcrm.dto.FinancialTransactionResponse;
import com.axelcrm.entity.BankAccount;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.entity.ChartOfAccount;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.BankAccountRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.FinancialTransactionRepository;
import com.axelcrm.repository.ChartOfAccountRepository;
import com.axelcrm.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class FinancialTransactionService {

    private final FinancialTransactionRepository financialTransactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;
    private final CommissionService commissionService;

    public Page<FinancialTransactionResponse> findAll(UUID organizationId, Pageable pageable) {
        return financialTransactionRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public FinancialTransactionResponse findById(UUID organizationId, UUID id) {
        return financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", id));
    }

    @Transactional
    public FinancialTransactionResponse create(UUID organizationId, FinancialTransactionRequest request) {
        FinancialTransaction tx = new FinancialTransaction();
        tx.setDescription(request.description());
        tx.setTransactionType(request.transactionType());
        tx.setAmount(request.amount());
        tx.setTransactionDate(request.transactionDate());
        tx.setDueDate(request.dueDate());
        tx.setPaidAt(request.paidAt());
        tx.setPaid(request.paid());
        tx.setCategory(request.category());

        if (request.bankAccountId() != null) {
            BankAccount bankAccount = bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.bankAccountId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", request.bankAccountId()));
            tx.setBankAccount(bankAccount);

            // Update bank account balance
            if (tx.isPaid()) {
                if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.INCOME) {
                    bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().add(tx.getAmount()));
                } else if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.EXPENSE) {
                    bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().subtract(tx.getAmount()));
                }
                bankAccountRepository.save(bankAccount);
            }
        }
        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            tx.setClient(client);
        }
        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));
            tx.setDeal(deal);
        }

        if (request.chartAccountId() != null) {
            ChartOfAccount chartAccount = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.chartAccountId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "id", request.chartAccountId()));
            tx.setChartAccount(chartAccount);
        }

        tx = financialTransactionRepository.save(tx);
        commissionService.calculateCommissionsForTransaction(tx);
        return toResponse(tx);
    }

    @Transactional
    public FinancialTransactionResponse update(UUID organizationId, UUID id, FinancialTransactionRequest request) {
        FinancialTransaction tx = financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", id));

        BankAccount oldBankAccount = tx.getBankAccount();
        boolean oldPaid = tx.isPaid();
        com.axelcrm.entity.enums.TransactionType oldType = tx.getTransactionType();
        java.math.BigDecimal oldAmount = tx.getAmount();

        tx.setDescription(request.description());
        tx.setTransactionType(request.transactionType());
        tx.setAmount(request.amount());
        tx.setTransactionDate(request.transactionDate());
        tx.setDueDate(request.dueDate());
        tx.setPaidAt(request.paidAt());
        tx.setPaid(request.paid());
        tx.setCategory(request.category());

        // Revert old bank account balance effect
        if (oldPaid && oldBankAccount != null) {
            if (oldType == com.axelcrm.entity.enums.TransactionType.INCOME) {
                oldBankAccount.setCurrentBalance(oldBankAccount.getCurrentBalance().subtract(oldAmount));
            } else if (oldType == com.axelcrm.entity.enums.TransactionType.EXPENSE) {
                oldBankAccount.setCurrentBalance(oldBankAccount.getCurrentBalance().add(oldAmount));
            }
            bankAccountRepository.save(oldBankAccount);
        }

        if (request.bankAccountId() != null) {
            BankAccount bankAccount = bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.bankAccountId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", request.bankAccountId()));
            tx.setBankAccount(bankAccount);

            // Apply new bank account balance effect
            if (tx.isPaid()) {
                if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.INCOME) {
                    bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().add(tx.getAmount()));
                } else if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.EXPENSE) {
                    bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().subtract(tx.getAmount()));
                }
                bankAccountRepository.save(bankAccount);
            }
        } else {
            tx.setBankAccount(null);
        }

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            tx.setClient(client);
        } else {
            tx.setClient(null);
        }

        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));
            tx.setDeal(deal);
        } else {
            tx.setDeal(null);
        }

        if (request.chartAccountId() != null) {
            ChartOfAccount chartAccount = chartOfAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.chartAccountId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("ChartOfAccount", "id", request.chartAccountId()));
            tx.setChartAccount(chartAccount);
        } else {
            tx.setChartAccount(null);
        }

        tx = financialTransactionRepository.save(tx);
        commissionService.calculateCommissionsForTransaction(tx);
        return toResponse(tx);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        FinancialTransaction tx = financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialTransaction", "id", id));

        // Revert bank account balance effect
        if (tx.isPaid() && tx.getBankAccount() != null) {
            BankAccount bankAccount = tx.getBankAccount();
            if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.INCOME) {
                bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().subtract(tx.getAmount()));
            } else if (tx.getTransactionType() == com.axelcrm.entity.enums.TransactionType.EXPENSE) {
                bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().add(tx.getAmount()));
            }
            bankAccountRepository.save(bankAccount);
        }

        tx.setDeletedAt(java.time.LocalDateTime.now());
        financialTransactionRepository.save(tx);
    }

    private FinancialTransactionResponse toResponse(FinancialTransaction tx) {
        return new FinancialTransactionResponse(
                tx.getId(),
                tx.getDescription(),
                tx.getTransactionType(),
                tx.getAmount(),
                tx.getTransactionDate(),
                tx.getDueDate(),
                tx.getPaidAt(),
                tx.isPaid(),
                tx.getCategory(),
                tx.getBankAccount() != null ? tx.getBankAccount().getId() : null,
                tx.getBankAccount() != null ? tx.getBankAccount().getName() : null,
                tx.getClient() != null ? tx.getClient().getId() : null,
                tx.getClient() != null ? tx.getClient().getName() : null,
                tx.getDeal() != null ? tx.getDeal().getId() : null,
                tx.getDeal() != null ? tx.getDeal().getTitle() : null,
                tx.getChartAccount() != null ? tx.getChartAccount().getId() : null,
                tx.getChartAccount() != null ? tx.getChartAccount().getName() : null,
                tx.getChartAccount() != null ? tx.getChartAccount().getCode() : null,
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }
}
