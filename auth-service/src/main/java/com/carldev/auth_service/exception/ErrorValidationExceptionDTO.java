package com.carldev.auth_service.exception;

public record ErrorValidationExceptionDTO(
        int Status,
        String field,
        String message,
        long timestamp
) {
}
