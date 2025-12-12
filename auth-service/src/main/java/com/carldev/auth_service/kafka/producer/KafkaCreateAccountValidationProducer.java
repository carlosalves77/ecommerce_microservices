package com.carldev.auth_service.kafka.producer;

import com.carldev.auth_service.kafka.eventsDTO.CreateAccountValidationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class KafkaCreateAccountValidationProducer {

    private static final String TOPIC = "auth-producer";
    private final KafkaTemplate<String, CreateAccountValidationEvent> kafkaTemplate;

    public KafkaCreateAccountValidationProducer(KafkaTemplate<String, CreateAccountValidationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @EventListener
    public void handleCreateAccountValidationEvent(CreateAccountValidationEvent createAccountValidationEvent) {
        kafkaTemplate.send(TOPIC, createAccountValidationEvent.userName(), createAccountValidationEvent);

    }
}
