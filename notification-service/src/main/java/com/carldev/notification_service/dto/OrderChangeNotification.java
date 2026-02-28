package com.carldev.notification_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderChangeNotification(
        UUID orderId,
        String userEmail,
        String userName,
        String orderNumber,
        BigDecimal totalAmount,
        String status,
        String updateAt
) {
}
