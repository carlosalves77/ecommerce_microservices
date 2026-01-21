package com.carldev.payment_service.kafka.producer;

import com.carldev.payment_service.dto.request.ItemDTO;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID orderId,
        String userEmail,
        String userName,
        UUID orderNumber,
        String totalAmount,
        String currency,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        List<ItemDTO> items
) {
}
