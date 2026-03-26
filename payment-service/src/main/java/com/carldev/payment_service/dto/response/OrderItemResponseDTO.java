package com.carldev.payment_service.dto.response;

import java.math.BigDecimal;

public record OrderItemResponseDTO (
        Long id,
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        PaymentResponseDTO payment
){
}
