package com.saurabh.ExpenseTracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    private Long categoryId;
    private String categoryName;

    public ExpenseResponse(
            Long id,
            BigDecimal amount,
            LocalDate date,
            String description,
            Long categoryId,
            String categoryName
    ) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}