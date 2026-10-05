package com.saurabh.ExpenseTracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than 0"
    )
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(
            max = 500,
            message = "Description cannot exceed 500 characters"
    )
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}