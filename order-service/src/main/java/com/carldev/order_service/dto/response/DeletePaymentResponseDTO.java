package com.carldev.order_service.dto.response;

public record DeletePaymentResponseDTO(
        String userName,
        String orderNumber
) {
}
