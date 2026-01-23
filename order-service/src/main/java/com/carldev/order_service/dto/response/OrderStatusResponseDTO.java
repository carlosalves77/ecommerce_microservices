package com.carldev.order_service.dto.response;

import com.carldev.order_service.util.OrderStatus;

import java.util.UUID;

public record OrderStatusResponseDTO(
        UUID userId,
        String email,
        String userName,
        long orderNumber,
        OrderStatus status
) {
}
