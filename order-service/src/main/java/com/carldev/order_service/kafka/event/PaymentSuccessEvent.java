package com.carldev.order_service.kafka.event;

import com.carldev.order_service.dto.request.OrderItemSuccessPaymentConsumer;
import com.carldev.order_service.dto.response.AddressPaymentSuccess;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessEvent(
        UUID orderId,
        String userEmail,
        String userName,
        String orderNumber,
        String totalAmount,
        String currency,
        String paymentMethod,
        String cardLast4,
        String paidAt,
        AddressPaymentSuccess addressPaymentSuccess,
        List<OrderItemSuccessPaymentConsumer> items

) {

}
