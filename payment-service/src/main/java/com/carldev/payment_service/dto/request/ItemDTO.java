package com.carldev.payment_service.dto.request;

import java.math.BigDecimal;

public record ItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
