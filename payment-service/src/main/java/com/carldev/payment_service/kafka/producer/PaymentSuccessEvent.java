package com.carldev.payment_service.kafka.producer;

import com.carldev.payment_service.dto.request.ItemDTO;
import com.carldev.payment_service.kafka.eventsDTO.PaymentCreateEvent;

import java.util.List;
import java.util.UUID;

public record PaymentSuccessEvent(
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

    public static PaymentSuccessEvent fromEntity(PaymentCreateEvent requestDTO) {

        return new PaymentSuccessEvent(
                requestDTO.orderId(),
                requestDTO.userEmail(),
                requestDTO.userName(),
                requestDTO.totalAmount(),
                requestDTO.currency(),
                requestDTO.paymentMethod(),
                requestDTO.cardLast4(),
                requestDTO.paidAt(),
                requestDTO.items()
        );
    }
}
