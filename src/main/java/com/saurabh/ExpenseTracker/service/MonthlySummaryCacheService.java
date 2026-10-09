package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class MonthlySummaryCacheService {

    private final ExpenseRepository expenseRepository;

    public MonthlySummaryCacheService(
            ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Cacheable(
            value = "monthlySummary",
            key = "#categoryId + ':' + #month"
    )
    public MonthlySummaryResponse getMonthlySummary(
            Long categoryId,
            String categoryName,
            BigDecimal budgetLimit,
            YearMonth month) {

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        BigDecimal monthlyTotal =
                expenseRepository.calculateMonthlyTotal(
                        categoryId,
                        startDate,
                        endDate
                );

        boolean budgetExceeded =
                monthlyTotal.compareTo(budgetLimit) > 0;

        return new MonthlySummaryResponse(
                categoryId,
                categoryName,
                month.toString(),
                monthlyTotal,
                budgetLimit,
                budgetExceeded
        );
    }

    @CacheEvict(
            value = "monthlySummary",
            key = "#categoryId + ':' + #month"
    )
    public void evictMonthlySummary(
            Long categoryId,
            YearMonth month) {
        // Spring evicts this specific cache entry.
    }

    @CacheEvict(
            value = "monthlySummary",
            allEntries = true
    )
    public void evictAllMonthlySummaries() {
        // Used when category name or budget changes.
    }

}
