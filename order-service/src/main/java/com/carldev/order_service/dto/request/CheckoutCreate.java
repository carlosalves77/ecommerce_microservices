package com.carldev.order_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckoutCreate(
        UUID userId,
        String email,
        String userName,
        Long orderNumber,
        BigDecimal totalAmount,
        List<CheckoutItems> items
) {
}
