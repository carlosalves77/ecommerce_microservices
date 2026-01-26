package com.carldev.payment_service.kafka.producer;

import com.carldev.payment_service.dto.request.ItemDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID orderId,
        String userEmail,
        String userName,
        Long orderNumber,
        String totalAmount,
        String currency,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        List<ItemDTO> items
) {
}
