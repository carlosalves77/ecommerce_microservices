package com.carldev.auth_service.kafka.producer;

import com.carldev.auth_service.kafka.eventsDTO.ResetPasswordEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaResetPasswordProducer {

    private static final String TOPIC = "auth-reset-password";
    private final KafkaTemplate<String, ResetPasswordEvent> kafkaTemplate;

    public KafkaResetPasswordProducer(KafkaTemplate<String, ResetPasswordEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @EventListener
    public void handleResetPasswordEvent(ResetPasswordEvent passwordEvent) {
        kafkaTemplate.send(TOPIC, "Reset Password Token", passwordEvent);

    }
}
