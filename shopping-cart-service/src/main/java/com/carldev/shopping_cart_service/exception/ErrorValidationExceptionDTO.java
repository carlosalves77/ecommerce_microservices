package com.carldev.shopping_cart_service.exception;

public record ErrorValidationExceptionDTO(
        int Status,
        String field,
        String message,
        long timestamp
) {
}
