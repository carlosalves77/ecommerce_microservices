package com.carldev.order_service.dto.request;

import com.carldev.order_service.util.OrderStatus;

public record OrderStatusRequestDTO(
        OrderStatus status
) {
}
