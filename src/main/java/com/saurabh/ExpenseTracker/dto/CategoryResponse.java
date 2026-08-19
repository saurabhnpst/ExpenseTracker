package com.saurabh.ExpenseTracker.dto;

import java.math.BigDecimal;

public class CategoryResponse {

    private Long id;
    private String name;
    private BigDecimal budgetLimit;

    public CategoryResponse(
            Long id,
            String name,
            BigDecimal budgetLimit) {

        this.id = id;
        this.name = name;
        this.budgetLimit = budgetLimit;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }
}