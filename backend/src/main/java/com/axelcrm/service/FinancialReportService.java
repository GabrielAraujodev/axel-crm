package com.axelcrm.service;

import com.axelcrm.dto.CashFlowReportResponse;
import com.axelcrm.dto.CashFlowReportResponse.CashFlowPeriod;
import com.axelcrm.dto.IncomeStatementResponse;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.entity.enums.TransactionType;
import com.axelcrm.repository.FinancialTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final FinancialTransactionRepository financialTransactionRepository;

    /**
     * DFC — Demonstrativo de Fluxo de Caixa.
     * Groups transactions by month within the date range.
     */
    public CashFlowReportResponse generateCashFlow(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        List<FinancialTransaction> transactions = financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(organizationId, startDate, endDate);

        Map<YearMonth, CashFlowPeriod> periodMap = new LinkedHashMap<>();

        for (FinancialTransaction tx : transactions) {
            YearMonth ym = YearMonth.from(tx.getTransactionDate());
            CashFlowPeriod period = periodMap.computeIfAbsent(ym, k ->
                    new CashFlowPeriod(k.toString(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

            if (tx.getTransactionType() == TransactionType.INCOME || tx.getTransactionType() == TransactionType.REFUND) {
                period = new CashFlowPeriod(
                        period.period(),
                        period.inflows().add(tx.getAmount()),
                        period.outflows(),
                        period.inflows().add(tx.getAmount()).subtract(period.outflows()));
            } else {
                period = new CashFlowPeriod(
                        period.period(),
                        period.inflows(),
                        period.outflows().add(tx.getAmount()),
                        period.inflows().subtract(period.outflows().add(tx.getAmount())));
            }
            periodMap.put(ym, period);
        }

        BigDecimal totalInflows = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME || t.getTransactionType() == TransactionType.REFUND)
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutflows = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE
                        || t.getTransactionType() == TransactionType.TRANSFER
                        || t.getTransactionType() == TransactionType.ADJUSTMENT)
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CashFlowReportResponse(
                startDate, endDate,
                List.copyOf(periodMap.values()),
                totalInflows, totalOutflows,
                totalInflows.subtract(totalOutflows));
    }

    /**
     * DRE — Demonstrativo de Resultado do Exercício.
     * Sums incomes vs expenses within the date range.
     */
    public IncomeStatementResponse generateIncomeStatement(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        List<FinancialTransaction> transactions = financialTransactionRepository
                .findByOrganization_IdAndTransactionDateBetweenAndDeletedAtIsNull(organizationId, startDate, endDate);

        BigDecimal totalRevenue = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new IncomeStatementResponse(
                startDate, endDate,
                totalRevenue, totalExpenses,
                totalRevenue.subtract(totalExpenses));
    }
}
