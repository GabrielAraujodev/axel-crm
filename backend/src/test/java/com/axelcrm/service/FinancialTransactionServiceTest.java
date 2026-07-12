package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.FinancialTransactionRequest;
import com.axelcrm.dto.FinancialTransactionResponse;
import com.axelcrm.entity.BankAccount;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.entity.enums.TransactionType;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.BankAccountRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.FinancialTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionServiceTest {

    @Mock
    FinancialTransactionRepository financialTransactionRepository;

    @Mock
    BankAccountRepository bankAccountRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    DealRepository dealRepository;

    @Mock
    com.axelcrm.repository.ChartOfAccountRepository chartOfAccountRepository;

    @Mock
    CommissionService commissionService;

    @InjectMocks
    FinancialTransactionService financialTransactionService;

    @Captor
    ArgumentCaptor<FinancialTransaction> txCaptor;

    private final UUID orgId = UUID.randomUUID();
    private final UUID txId = UUID.randomUUID();

    private Organization createOrg() {
        var org = new Organization();
        org.setId(orgId);
        return org;
    }

    private FinancialTransaction createTx() {
        var org = createOrg();
        var tx = new FinancialTransaction();
        tx.setId(txId);
        tx.setDescription("Venda de serviços");
        tx.setTransactionType(TransactionType.INCOME);
        tx.setAmount(BigDecimal.valueOf(5000));
        tx.setTransactionDate(LocalDate.now());
        tx.setPaid(true);
        tx.setOrganization(org);
        return tx;
    }

    @Test
    void findAll_ShouldReturnPagedTransactions() {
        var tx = createTx();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(tx));

        when(financialTransactionRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<FinancialTransactionResponse> result = financialTransactionService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Venda de serviços", result.getContent().getFirst().description());
    }

    @Test
    void findById_ShouldReturnTransaction() {
        var tx = createTx();
        when(financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(txId, orgId))
                .thenReturn(Optional.of(tx));

        FinancialTransactionResponse result = financialTransactionService.findById(orgId, txId);

        assertNotNull(result);
        assertEquals(txId, result.id());
        assertEquals(TransactionType.INCOME, result.transactionType());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(txId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> financialTransactionService.findById(orgId, txId));
    }

    @Test
    void create_ShouldSaveAndReturnTransaction() {
        var request = new FinancialTransactionRequest(
                "Venda de serviços", TransactionType.INCOME, BigDecimal.valueOf(5000),
                LocalDate.now(), null, null, false, null, null, null, null);

        var saved = new FinancialTransaction();
        saved.setId(txId);
        saved.setDescription("Venda de serviços");
        saved.setTransactionType(TransactionType.INCOME);
        saved.setAmount(BigDecimal.valueOf(5000));
        saved.setTransactionDate(LocalDate.now());
        saved.setPaid(false);

        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenReturn(saved);

        FinancialTransactionResponse result = financialTransactionService.create(orgId, request);

        assertNotNull(result);
        assertEquals("Venda de serviços", result.description());
        assertEquals(TransactionType.INCOME, result.transactionType());
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    void create_WithBankAccountAndPaid_ShouldUpdateBalance() {
        var bankAccount = new BankAccount();
        bankAccount.setId(UUID.randomUUID());
        bankAccount.setName("Conta Principal");
        bankAccount.setCurrentBalance(BigDecimal.valueOf(10000));

        var request = new FinancialTransactionRequest(
                "Receita", TransactionType.INCOME, BigDecimal.valueOf(2000),
                LocalDate.now(), null, LocalDateTime.now(), true, null,
                bankAccount.getId(), null, null);

        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(bankAccount.getId(), orgId))
                .thenReturn(Optional.of(bankAccount));
        when(financialTransactionRepository.save(any(FinancialTransaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        FinancialTransactionResponse result = financialTransactionService.create(orgId, request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(12000), bankAccount.getCurrentBalance());
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void create_WithExpenseAndPaid_ShouldDecreaseBalance() {
        var bankAccount = new BankAccount();
        bankAccount.setId(UUID.randomUUID());
        bankAccount.setName("Conta Principal");
        bankAccount.setCurrentBalance(BigDecimal.valueOf(10000));

        var request = new FinancialTransactionRequest(
                "Despesa", TransactionType.EXPENSE, BigDecimal.valueOf(3000),
                LocalDate.now(), null, LocalDateTime.now(), true, null,
                bankAccount.getId(), null, null);

        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(bankAccount.getId(), orgId))
                .thenReturn(Optional.of(bankAccount));
        when(financialTransactionRepository.save(any(FinancialTransaction.class)))
                .thenAnswer(i -> i.getArgument(0));

        FinancialTransactionResponse result = financialTransactionService.create(orgId, request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(7000), bankAccount.getCurrentBalance());
        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    void update_ShouldModifyAndReturnTransaction() {
        var existing = createTx();
        var request = new FinancialTransactionRequest(
                "Updated", TransactionType.EXPENSE, BigDecimal.valueOf(1000),
                LocalDate.now(), null, null, false, null, null, null, null);

        when(financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(txId, orgId))
                .thenReturn(Optional.of(existing));
        when(financialTransactionRepository.save(any(FinancialTransaction.class))).thenAnswer(i -> i.getArgument(0));

        FinancialTransactionResponse result = financialTransactionService.update(orgId, txId, request);

        assertNotNull(result);
        assertEquals("Updated", result.description());
        assertEquals(TransactionType.EXPENSE, result.transactionType());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var tx = createTx();
        when(financialTransactionRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(txId, orgId))
                .thenReturn(Optional.of(tx));

        financialTransactionService.delete(orgId, txId);

        assertNotNull(tx.getDeletedAt());
        verify(financialTransactionRepository).save(tx);
    }
}
