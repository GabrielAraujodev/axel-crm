package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.CashFlowReportResponse;
import com.axelcrm.dto.IncomeStatementResponse;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.entity.enums.TransactionType;
import com.axelcrm.repository.FinancialTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceTest {

    @Mock
    FinancialTransactionRepository financialTransactionRepository;

    @InjectMocks
    FinancialReportService financialReportService;

    private final UUID orgId = UUID.randomUUID();

    private FinancialTransaction createTx(TransactionType type, int amount, LocalDate date) {
        var tx = new FinancialTransaction();
        tx.setId(UUID.randomUUID());
        tx.setTransactionType(type);
        tx.setAmount(BigDecimal.valueOf(amount));
        tx.setTransactionDate(date);
        tx.setDescription("Test " + type);
        tx.setPaid(true);
        return tx;
    }

    @Test
    void generateCashFlow_ShouldGroupByMonth() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 3, 31);

        var tx1 = createTx(TransactionType.INCOME, 5000, LocalDate.of(2026, 1, 15));
        var tx2 = createTx(TransactionType.EXPENSE, 2000, LocalDate.of(2026, 1, 20));
        var tx3 = createTx(TransactionType.INCOME, 3000, LocalDate.of(2026, 2, 10));
        var tx4 = createTx(TransactionType.EXPENSE, 1000, LocalDate.of(2026, 2, 15));

        when(financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(orgId, start, end))
                .thenReturn(List.of(tx1, tx2, tx3, tx4));

        CashFlowReportResponse result = financialReportService.generateCashFlow(orgId, start, end);

        assertEquals(start, result.startDate());
        assertEquals(end, result.endDate());
        assertEquals(2, result.periods().size());
        assertEquals(BigDecimal.valueOf(8000), result.totalInflows());
        assertEquals(BigDecimal.valueOf(3000), result.totalOutflows());
        assertEquals(BigDecimal.valueOf(5000), result.netCashFlow());
    }

    @Test
    void generateCashFlow_ShouldReturnEmptyWhenNoTransactions() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 1, 31);

        when(financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(orgId, start, end))
                .thenReturn(List.of());

        CashFlowReportResponse result = financialReportService.generateCashFlow(orgId, start, end);

        assertEquals(BigDecimal.ZERO, result.totalInflows());
        assertEquals(BigDecimal.ZERO, result.totalOutflows());
        assertEquals(BigDecimal.ZERO, result.netCashFlow());
        assertTrue(result.periods().isEmpty());
    }

    @Test
    void generateIncomeStatement_ShouldReturnRevenueMinusExpenses() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 12, 31);

        var tx1 = createTx(TransactionType.INCOME, 10000, LocalDate.of(2026, 6, 1));
        var tx2 = createTx(TransactionType.INCOME, 5000, LocalDate.of(2026, 6, 15));
        var tx3 = createTx(TransactionType.EXPENSE, 4000, LocalDate.of(2026, 6, 20));
        var tx4 = createTx(TransactionType.EXPENSE, 1000, LocalDate.of(2026, 7, 5));

        when(financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(orgId, start, end))
                .thenReturn(List.of(tx1, tx2, tx3, tx4));

        IncomeStatementResponse result = financialReportService.generateIncomeStatement(orgId, start, end);

        assertEquals(BigDecimal.valueOf(15000), result.totalRevenue());
        assertEquals(BigDecimal.valueOf(5000), result.totalExpenses());
        assertEquals(BigDecimal.valueOf(10000), result.netResult());
    }

    @Test
    void generateIncomeStatement_ShouldReturnZeroWhenEmpty() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 12, 31);

        when(financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(orgId, start, end))
                .thenReturn(List.of());

        IncomeStatementResponse result = financialReportService.generateIncomeStatement(orgId, start, end);

        assertEquals(BigDecimal.ZERO, result.totalRevenue());
        assertEquals(BigDecimal.ZERO, result.totalExpenses());
        assertEquals(BigDecimal.ZERO, result.netResult());
    }
}
