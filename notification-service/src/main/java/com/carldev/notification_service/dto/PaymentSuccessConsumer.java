package com.carldev.notification_service.dto;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessConsumer(
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
        List<ItemDTO> items
) {

}
