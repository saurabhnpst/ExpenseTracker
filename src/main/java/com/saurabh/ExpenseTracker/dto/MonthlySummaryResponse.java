package com.saurabh.ExpenseTracker.dto;

import java.math.BigDecimal;

public class MonthlySummaryResponse {

    private Long categoryId;
    private String categoryName;
    private String month;
    private BigDecimal monthlyTotal;
    private BigDecimal budgetLimit;
    private boolean budgetExceeded;

    public MonthlySummaryResponse(
            Long categoryId,
            String categoryName,
            String month,
            BigDecimal monthlyTotal,
            BigDecimal budgetLimit,
            boolean budgetExceeded
    ) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.month = month;
        this.monthlyTotal = monthlyTotal;
        this.budgetLimit = budgetLimit;
        this.budgetExceeded = budgetExceeded;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getMonthlyTotal() {
        return monthlyTotal;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public boolean isBudgetExceeded() {
        return budgetExceeded;
    }
}