package com.carldev.payment_service.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        UUID userId,
        String email,
        String userName,
        BigDecimal amount,
        Long orderNumber,
        String paymentType,
        Instant createdAt,
        Instant updatedAt
) {
}
