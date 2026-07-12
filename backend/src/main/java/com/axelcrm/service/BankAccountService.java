package com.axelcrm.service;

import com.axelcrm.dto.BankAccountRequest;
import com.axelcrm.dto.BankAccountResponse;
import com.axelcrm.entity.BankAccount;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public Page<BankAccountResponse> findAll(UUID organizationId, Pageable pageable) {
        return bankAccountRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public BankAccountResponse findById(UUID organizationId, UUID id) {
        return bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", id));
    }

    @Transactional
    public BankAccountResponse create(UUID organizationId, BankAccountRequest request) {
        BankAccount account = new BankAccount();
        account.setName(request.name());
        account.setBankName(request.bankName());
        account.setAccountNumber(request.accountNumber());
        account.setAgency(request.agency());
        account.setCurrentBalance(request.currentBalance() != null ? request.currentBalance() : BigDecimal.ZERO);
        account.setActive(request.active());

        account = bankAccountRepository.save(account);
        return toResponse(account);
    }

    @Transactional
    public BankAccountResponse update(UUID organizationId, UUID id, BankAccountRequest request) {
        BankAccount account = bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", id));

        account.setName(request.name());
        account.setBankName(request.bankName());
        account.setAccountNumber(request.accountNumber());
        account.setAgency(request.agency());
        if (request.currentBalance() != null) {
            account.setCurrentBalance(request.currentBalance());
        }
        account.setActive(request.active());

        account = bankAccountRepository.save(account);
        return toResponse(account);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        BankAccount account = bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", id));
        account.setDeletedAt(java.time.LocalDateTime.now());
        bankAccountRepository.save(account);
    }

    private BankAccountResponse toResponse(BankAccount account) {
        return new BankAccountResponse(
                account.getId(),
                account.getName(),
                account.getBankName(),
                account.getAccountNumber(),
                account.getAgency(),
                account.getCurrentBalance(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
