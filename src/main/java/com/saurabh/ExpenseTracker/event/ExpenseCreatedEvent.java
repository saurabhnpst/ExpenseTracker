package com.saurabh.ExpenseTracker.event;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseCreatedEvent(
        Long expenseId,
        Long categoryId,
        BigDecimal amount,
        LocalDate date,
        String description
) {}
