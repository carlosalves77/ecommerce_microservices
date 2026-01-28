package com.carldev.auth_service.kafka.eventsDTO;


public record CreateAccountValidationEvent(
        String accountValidation,
        String userName,
        String email
) {

}
