package com.carldev.order_service.dto.request;

import com.carldev.order_service.kafka.event.AddressEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckoutCreate(
        UUID userId,
        String email,
        String userName,
        Long orderNumber,
        BigDecimal totalAmount,
        AddressEvent addressResponseDTO,
        List<CheckoutItems> items
) {
}
