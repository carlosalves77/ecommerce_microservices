package com.carldev.payment_service.dto.request;

public record AddItemRequestDTO(
        String sku,
        int quantity
) {
}
