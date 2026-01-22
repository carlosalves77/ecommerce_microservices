package com.carldev.order_service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducer {

    private static final String TOPIC = "payment-success-notification";
    private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @EventListener
    public void handleProductCreatedEvent(PaymentSuccessEvent paymentSuccessEvent) {
        kafkaTemplate.send(TOPIC, "Payment Success Notification", paymentSuccessEvent);
    }
}
