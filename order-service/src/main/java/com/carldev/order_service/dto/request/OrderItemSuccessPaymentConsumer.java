package com.carldev.order_service.dto.request;

import java.math.BigDecimal;

public record OrderItemSuccessPaymentConsumer(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
