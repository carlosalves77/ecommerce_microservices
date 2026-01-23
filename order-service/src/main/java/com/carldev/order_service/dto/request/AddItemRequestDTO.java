package com.carldev.order_service.dto.request;

public record AddItemRequestDTO(
        String sku,
        int quantity
) {
}
