package com.carldev.order_service.dto.request;

import java.math.BigDecimal;

public record OrderStatusItem(
        String skuCode,
        Integer quantity,
        BigDecimal price
) {
}
