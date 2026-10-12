package com.saurabh.ExpenseTracker.messaging.expense;
import com.saurabh.ExpenseTracker.event.ExpenseCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class ExpenseCreatedEventListener {

    private final KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate;

    public ExpenseCreatedEventListener(
            KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(ExpenseCreatedEvent event) {
        kafkaTemplate.send(
                "expense-created",
                event.categoryId().toString(),
                event
        );
    }
}