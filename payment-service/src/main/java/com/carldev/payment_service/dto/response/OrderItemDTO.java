package com.carldev.payment_service.dto.response;

import java.math.BigDecimal;

public record OrderItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
