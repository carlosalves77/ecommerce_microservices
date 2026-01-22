package com.carldev.order_service.dto.request;

import java.math.BigDecimal;

public record CheckoutItems (
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
