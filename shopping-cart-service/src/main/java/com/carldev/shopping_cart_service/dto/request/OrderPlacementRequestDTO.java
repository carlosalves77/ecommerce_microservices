package com.carldev.shopping_cart_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPlacementRequestDTO(
        UUID userId,
        String email,
        String userName,
        BigDecimal totalAmount,
        List<OrderItemDTO> items
        ) {
}
