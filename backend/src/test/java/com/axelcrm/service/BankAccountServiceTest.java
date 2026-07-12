package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.BankAccountRequest;
import com.axelcrm.dto.BankAccountResponse;
import com.axelcrm.entity.BankAccount;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.BankAccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    BankAccountRepository bankAccountRepository;

    @InjectMocks
    BankAccountService bankAccountService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();

    private BankAccount createAccount() {
        var org = new Organization();
        org.setId(orgId);

        var account = new BankAccount();
        account.setId(accountId);
        account.setName("Conta Principal");
        account.setBankName("Banco do Brasil");
        account.setAccountNumber("12345-6");
        account.setAgency("0001");
        account.setCurrentBalance(BigDecimal.valueOf(10000));
        account.setActive(true);
        account.setOrganization(org);
        return account;
    }

    @Test
    void findAll_ShouldReturnPagedAccounts() {
        var account = createAccount();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(account));

        when(bankAccountRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<BankAccountResponse> result = bankAccountService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Conta Principal", result.getContent().getFirst().name());
    }

    @Test
    void findById_ShouldReturnAccount() {
        var account = createAccount();
        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(accountId, orgId))
                .thenReturn(Optional.of(account));

        BankAccountResponse result = bankAccountService.findById(orgId, accountId);

        assertNotNull(result);
        assertEquals(accountId, result.id());
        assertEquals("Banco do Brasil", result.bankName());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(accountId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.findById(orgId, accountId));
    }

    @Test
    void create_ShouldSaveAndReturnAccount() {
        var request = new BankAccountRequest(
                "Nova Conta", "Itaú", "67890-1", "0002",
                BigDecimal.valueOf(5000), true);

        var saved = new BankAccount();
        saved.setId(accountId);
        saved.setName("Nova Conta");
        saved.setBankName("Itaú");
        saved.setAccountNumber("67890-1");
        saved.setAgency("0002");
        saved.setCurrentBalance(BigDecimal.valueOf(5000));
        saved.setActive(true);

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(saved);

        BankAccountResponse result = bankAccountService.create(orgId, request);

        assertNotNull(result);
        assertEquals("Nova Conta", result.name());
        assertEquals("Itaú", result.bankName());
    }

    @Test
    void update_ShouldModifyAndReturnAccount() {
        var request = new BankAccountRequest(
                "Updated", "Bradesco", "11111-2", "0003",
                BigDecimal.valueOf(8000), true);
        var existing = createAccount();

        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(accountId, orgId))
                .thenReturn(Optional.of(existing));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));

        BankAccountResponse result = bankAccountService.update(orgId, accountId, request);

        assertNotNull(result);
        assertEquals("Updated", result.name());
        assertEquals("Bradesco", result.bankName());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var account = createAccount();
        when(bankAccountRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(accountId, orgId))
                .thenReturn(Optional.of(account));

        bankAccountService.delete(orgId, accountId);

        assertNotNull(account.getDeletedAt());
        verify(bankAccountRepository).save(account);
    }
}
