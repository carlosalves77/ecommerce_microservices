package com.carldev.shopping_cart_service.kafka;

import com.carldev.shopping_cart_service.dto.OrderItemDTO;
import com.carldev.shopping_cart_service.dto.request.OrderPlacementRequestDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckOutCreatedEvent(
        UUID userId,
        BigDecimal totalAmount,
        List<OrderItemDTO> items
) {

    public static CheckOutCreatedEvent fromEntity(OrderPlacementRequestDTO requestDTO) {

        return new CheckOutCreatedEvent(
                requestDTO.userId(),
                requestDTO.totalAmount(),
                requestDTO.items()
        );
    }
}
