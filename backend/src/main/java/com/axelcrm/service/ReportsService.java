package com.axelcrm.service;

import com.axelcrm.dto.ReportItemResponse;
import com.axelcrm.dto.ReportResponse;
import com.axelcrm.entity.FinancialTransaction;
import com.axelcrm.entity.enums.TransactionType;
import com.axelcrm.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final FinancialTransactionRepository financialTransactionRepository;

    public ReportResponse getDre(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        // DRE is accrual-based (competência) -> use transactionDate (or dueDate if unpaid, but let's use transactionDate for all)
        List<FinancialTransaction> transactions = financialTransactionRepository
                .findByOrganization_IdAndDeletedAtIsNull(organizationId);

        List<FinancialTransaction> filtered = transactions.stream()
                .filter(t -> t.getTransactionDate() != null &&
                        !t.getTransactionDate().isBefore(startDate) &&
                        !t.getTransactionDate().isAfter(endDate))
                .collect(Collectors.toList());

        return buildReportResponse(filtered);
    }

    public ReportResponse getDfc(UUID organizationId, LocalDate startDate, LocalDate endDate) {
        // DFC is cash-based (caixa) -> only paid transactions based on transactionDate/paidAt date
        List<FinancialTransaction> transactions = financialTransactionRepository
                .findByOrganization_IdAndDeletedAtIsNull(organizationId);

        List<FinancialTransaction> filtered = transactions.stream()
                .filter(FinancialTransaction::isPaid)
                .filter(t -> t.getTransactionDate() != null &&
                        !t.getTransactionDate().isBefore(startDate) &&
                        !t.getTransactionDate().isAfter(endDate))
                .collect(Collectors.toList());

        return buildReportResponse(filtered);
    }

    private ReportResponse buildReportResponse(List<FinancialTransaction> transactions) {
        Map<String, ReportItemResponse> revenueMap = new HashMap<>();
        Map<String, ReportItemResponse> expenseMap = new HashMap<>();

        BigDecimal totalRevenues = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (FinancialTransaction tx : transactions) {
            String code = "9.9.99";
            String name = "Outros / Sem Classificação";

            if (tx.getChartAccount() != null) {
                code = tx.getChartAccount().getCode();
                name = tx.getChartAccount().getName();
            } else if (tx.getCategory() != null && !tx.getCategory().trim().isEmpty()) {
                code = "9.9.00";
                name = tx.getCategory();
            }

            BigDecimal amount = tx.getAmount();

            if (tx.getTransactionType() == TransactionType.INCOME) {
                totalRevenues = totalRevenues.add(amount);
                ReportItemResponse existing = revenueMap.get(code);
                if (existing != null) {
                    revenueMap.put(code, new ReportItemResponse(code, name, existing.total().add(amount)));
                } else {
                    revenueMap.put(code, new ReportItemResponse(code, name, amount));
                }
            } else if (tx.getTransactionType() == TransactionType.EXPENSE) {
                totalExpenses = totalExpenses.add(amount);
                ReportItemResponse existing = expenseMap.get(code);
                if (existing != null) {
                    expenseMap.put(code, new ReportItemResponse(code, name, existing.total().add(amount)));
                } else {
                    expenseMap.put(code, new ReportItemResponse(code, name, amount));
                }
            }
        }

        List<ReportItemResponse> revenues = new ArrayList<>(revenueMap.values());
        List<ReportItemResponse> expenses = new ArrayList<>(expenseMap.values());

        revenues.sort(Comparator.comparing(ReportItemResponse::accountCode));
        expenses.sort(Comparator.comparing(ReportItemResponse::accountCode));

        BigDecimal netResult = totalRevenues.subtract(totalExpenses);

        return new ReportResponse(revenues, expenses, totalRevenues, totalExpenses, netResult);
    }
}
