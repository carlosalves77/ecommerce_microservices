package com.carldev.order_service.kafka.event;

import com.carldev.order_service.util.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderChangeEvent(
        UUID orderId,
        String userEmail,
        String userName,
        long orderNumber,
        BigDecimal totalAmount,
        OrderStatus status,
        String updateAt

) {

}
