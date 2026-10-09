
package com.saurabh.ExpenseTracker.event;

import com.saurabh.ExpenseTracker.service.MonthlySummaryCacheService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MonthlySummaryCacheClearListener {

    private final MonthlySummaryCacheService cacheService;

    public MonthlySummaryCacheClearListener(
            MonthlySummaryCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheClear(MonthlySummaryCacheClearEvent event) {
        cacheService.evictAllMonthlySummaries();
    }
}
