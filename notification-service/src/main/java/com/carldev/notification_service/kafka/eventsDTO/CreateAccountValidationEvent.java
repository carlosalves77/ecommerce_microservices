package com.carldev.notification_service.kafka.eventsDTO;

public record CreateAccountValidationEvent (
        String accountValidation,
        String userName,
        String email
) {
}
