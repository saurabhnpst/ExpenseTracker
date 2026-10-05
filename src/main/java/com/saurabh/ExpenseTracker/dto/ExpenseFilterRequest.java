package com.saurabh.ExpenseTracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseFilterRequest {

    private Long categoryId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}