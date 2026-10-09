package com.saurabh.ExpenseTracker.event;

import com.saurabh.ExpenseTracker.service.MonthlySummaryCacheService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MonthlySummaryCacheInvalidationListener {

    private final MonthlySummaryCacheService cacheService;

    public MonthlySummaryCacheInvalidationListener(
            MonthlySummaryCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleMonthlySummaryInvalidation(
            MonthlySummaryInvalidationEvent event) {

        cacheService.evictMonthlySummary(
                event.categoryId(),
                event.month()
        );
    }
}
