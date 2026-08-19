package com.saurabh.ExpenseTracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100,
            message = "Category name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Budget limit is required")
    @DecimalMin(value = "0.0",
            message = "Budget limit cannot be negative")
    private BigDecimal budgetLimit;

    public String getName() {
        return name;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }
}