package com.carldev.order_service.dto.response;

import com.carldev.order_service.dto.request.OrderItem;
import com.carldev.order_service.util.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrdersResponseDTO(
        UUID userId,
        String email,
        String userName,
        String orderNumber,
        BigDecimal totalAmount,
        OrderStatus status,
        String updateAt,
        String createdAt,
        List<OrderItem> orderItems
) {
}
