package com.carldev.shopping_cart_service.dto.request;

import com.carldev.shopping_cart_service.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPlacementRequestDTO(
        UUID userId,
        BigDecimal totalAmount,
        List<OrderItemDTO> items
        ) {
}
