package com.carldev.shopping_cart_service.dto.request;

import com.carldev.shopping_cart_service.dto.response.AddressResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPlacementRequestDTO(
        UUID userId,
        String email,
        String userName,
        Long orderNumber,
        BigDecimal totalAmount,
        List<OrderItemDTO> items,
        AddressResponseDTO addressList
) {
}
