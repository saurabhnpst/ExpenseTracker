
package com.saurabh.ExpenseTracker.event;

import java.time.YearMonth;

public record MonthlySummaryInvalidationEvent(
        Long categoryId,
        YearMonth month) {
}
