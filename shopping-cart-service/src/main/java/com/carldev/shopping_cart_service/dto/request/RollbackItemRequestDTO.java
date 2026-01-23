package com.carldev.shopping_cart_service.dto.request;

public record RollbackItemRequestDTO(
        String sku,
        int quantity
) {
}
