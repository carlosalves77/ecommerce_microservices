package com.carldev.shopping_cart_service.dto.response;

public record ErrorExceptionResponseDTO(
        int Status,
        String message,
        long timestamp
) {
}
