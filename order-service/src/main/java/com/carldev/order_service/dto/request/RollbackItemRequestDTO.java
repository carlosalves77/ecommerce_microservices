package com.carldev.order_service.dto.request;

public record RollbackItemRequestDTO(
        String sku,
        int quantity
) {
}
