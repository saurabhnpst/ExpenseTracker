
package com.saurabh.ExpenseTracker.messaging.expense;

import com.saurabh.ExpenseTracker.event.ExpenseCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExpenseEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ExpenseEventConsumer.class);

    @KafkaListener(
            topics = "expense-created",
            groupId = "expense-tracker-group"
    )
    public void consumeExpenseCreated(ExpenseCreatedEvent event) {
        log.info(
                "Received expense-created event: expenseId={}, categoryId={}, amount={}",
                event.expenseId(),
                event.categoryId(),
                event.amount()
        );
    }
}
