package com.carldev.order_service.dto.request;

import java.math.BigDecimal;

public record OrderItem(
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}
