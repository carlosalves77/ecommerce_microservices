package com.carldev.payment_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderConsumeEvent(
        UUID userId,
        String email,
        String userName,
        BigDecimal totalAmount,
        List<OrderItemDTO> items
) {
}
