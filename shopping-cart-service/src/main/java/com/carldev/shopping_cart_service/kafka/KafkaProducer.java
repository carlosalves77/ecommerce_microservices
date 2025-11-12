package com.carldev.shopping_cart_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
public class KafkaProducer {

    private static final String TOPIC = "checkout-create";
    private final KafkaTemplate<String, CheckOutCreatedEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, CheckOutCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }



    @EventListener
    public void handleProductCreatedEvent(CheckOutCreatedEvent checkOutCreatedEvent) {
        log.info("Transação comitada! Enviando para o Kafka: {} ", checkOutCreatedEvent);

        kafkaTemplate.send(TOPIC, String.valueOf(checkOutCreatedEvent.userId()), checkOutCreatedEvent);

    }
}
