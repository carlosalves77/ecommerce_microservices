package com.carldev.order_service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaOrderStatusProducer {

    private static final String TOPIC = "order-change-notification";
    private final KafkaTemplate<String, OrderChangeEvent> kafkaTemplate;

    public KafkaOrderStatusProducer(KafkaTemplate<String, OrderChangeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener
    public void handleProductCreatedEvent(OrderChangeEvent orderChangeEvent) {
        kafkaTemplate.send(TOPIC, "Order Change Notification", orderChangeEvent);
    }
}
