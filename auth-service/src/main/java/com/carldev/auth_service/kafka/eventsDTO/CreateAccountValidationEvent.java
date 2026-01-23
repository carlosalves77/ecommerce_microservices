package com.carldev.auth_service.kafka.eventsDTO;


public record CreateAccountValidationEvent(
        String accountValidation,
        String userName,
        String email
) {

    public static CreateAccountValidationEvent fromEntity(String accountValidation, String userName,
                                                          String email) {
        return new CreateAccountValidationEvent(
                accountValidation,
                userName,
                email
        );
    }
}
