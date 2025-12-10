package com.carldev.notification_service.dto;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessConsumer(
        UUID orderId,
        String userEmail,
        String userName,
        String totalAmount,
        String currency,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        List<ItemDTO> items
) {

}
