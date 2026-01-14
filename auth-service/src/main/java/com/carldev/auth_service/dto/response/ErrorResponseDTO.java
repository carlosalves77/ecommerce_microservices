package com.carldev.auth_service.dto.response;

public record ErrorResponseDTO(
        int Status,
        String message,
        long timestamp
) {
}
