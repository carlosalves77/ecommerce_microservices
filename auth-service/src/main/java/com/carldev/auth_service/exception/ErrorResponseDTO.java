package com.carldev.auth_service.exception;

public record ErrorResponseDTO(
        int Status,
        String message,
        long timestamp
) {
}
