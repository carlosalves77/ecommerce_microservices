package com.carldev.shopping_cart_service.dto.request;

public record AddItemRequestDTO(
        String sku,
        int quantity
) {
}
