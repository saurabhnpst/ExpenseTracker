package com.saurabh.ExpenseTracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Category name must be between 2 and 100 characters"
    )
    private String name;

    @NotNull(message = "Budget limit is required")
    @DecimalMin(
            value = "0.0",
            message = "Budget limit cannot be negative"
    )
    private BigDecimal budgetLimit;
}