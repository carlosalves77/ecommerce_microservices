package com.carldev.payment_service.dto.request;

public record RollbackItemRequestDTO(
        String sku,
        int quantity
) {
}
