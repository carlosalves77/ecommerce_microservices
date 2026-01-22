package com.carldev.order_service.kafka.producer;

import com.carldev.order_service.dto.request.OrderItemSuccessPaymentConsumer;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID orderId,
        String userEmail,
        String userName,
        String orderNumber,
        String totalAmount,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        List<OrderItemSuccessPaymentConsumer> items

) {

}
