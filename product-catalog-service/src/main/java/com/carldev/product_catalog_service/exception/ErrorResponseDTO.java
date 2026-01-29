package com.carldev.product_catalog_service.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO (
        int Status,
        String message,
        LocalDateTime timestamp
){
}
