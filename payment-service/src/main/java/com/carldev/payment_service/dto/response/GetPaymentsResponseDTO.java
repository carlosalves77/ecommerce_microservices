package com.carldev.payment_service.dto.response;

import com.carldev.payment_service.utils.PaymentStatus;
import com.carldev.payment_service.utils.PaymentType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetPaymentsResponseDTO(
        String id,
        BigDecimal amount,
        PaymentStatus paymentStatus,
        Instant createdAt,
        PaymentType paymentType
) {

    public GetPaymentsResponseDTO {
        if (PaymentStatus.PENDING.equals(paymentStatus)) {
            paymentType = null;
        }
    }
}
