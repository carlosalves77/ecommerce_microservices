package com.carldev.order_service.dto.request;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessConsumer (
        UUID orderId,
        String userEmail,
        String userName,
        Long orderNumber,
        String totalAmount,
        String currency,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        List<OrderItemSuccessPaymentConsumer> items
) {

}
