package com.carldev.payment_service.dto.request;

import java.util.List;
import java.util.UUID;

public record PaymentCreateEvent(
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
