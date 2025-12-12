package com.carldev.payment_service.kafka.eventsDTO;

import java.math.BigDecimal;

public record OrderItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
