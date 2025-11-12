package com.carldev.shopping_cart_service.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
