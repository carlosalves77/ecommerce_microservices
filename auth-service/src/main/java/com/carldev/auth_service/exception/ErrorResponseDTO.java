package com.carldev.auth_service.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int Status,
        String message,
        LocalDateTime timestamp
) {
}
