package com.carldev.notification_service.dto;

public record CreateAccountValidationEvent (
        String accountValidation,
        String userName,
        String email
) {
}
