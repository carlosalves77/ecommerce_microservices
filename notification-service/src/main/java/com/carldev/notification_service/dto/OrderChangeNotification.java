package com.carldev.notification_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderChangeNotification(
        UUID orderId,
        String userEmail,
        String userName,
        long orderNumber,
        BigDecimal totalAmount,
        String status,
        String updateAt
) {
}
