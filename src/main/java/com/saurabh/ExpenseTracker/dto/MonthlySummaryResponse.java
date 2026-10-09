package com.saurabh.ExpenseTracker.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MonthlySummaryResponse {

    private final Long categoryId;
    private final String categoryName;
    private final String month;
    private final BigDecimal monthlyTotal;
    private final BigDecimal budgetLimit;
    private final boolean budgetExceeded;

    @JsonCreator
    public MonthlySummaryResponse(
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("categoryName") String categoryName,
            @JsonProperty("month") String month,
            @JsonProperty("monthlyTotal") BigDecimal monthlyTotal,
            @JsonProperty("budgetLimit") BigDecimal budgetLimit,
            @JsonProperty("budgetExceeded") boolean budgetExceeded) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.month = month;
        this.monthlyTotal = monthlyTotal;
        this.budgetLimit = budgetLimit;
        this.budgetExceeded = budgetExceeded;
    }

    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getMonth() { return month; }
    public BigDecimal getMonthlyTotal() { return monthlyTotal; }
    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public boolean isBudgetExceeded() { return budgetExceeded; }
}