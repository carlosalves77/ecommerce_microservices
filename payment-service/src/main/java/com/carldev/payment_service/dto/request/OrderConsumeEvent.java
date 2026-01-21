package com.carldev.payment_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderConsumeEvent(
        UUID userId,
        String email,
        String userName,
        UUID orderNumber,
        BigDecimal totalAmount,
        List<OrderItemDTO> items
) {
}
