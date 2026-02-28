package com.carldev.shopping_cart_service.kafka;

import com.carldev.shopping_cart_service.dto.request.OrderItemDTO;
import com.carldev.shopping_cart_service.dto.request.OrderPlacementRequestDTO;
import com.carldev.shopping_cart_service.dto.response.AddressResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CheckOutCreatedEvent(
        UUID userId,
        String email,
        String userName,
        Long orderNumber,
        BigDecimal totalAmount,
        AddressResponseDTO addressResponseDTO,
        List<OrderItemDTO> items

) {

    public static CheckOutCreatedEvent fromEntity(OrderPlacementRequestDTO requestDTO) {

        return new CheckOutCreatedEvent(
                requestDTO.userId(),
                requestDTO.email(),
                requestDTO.userName(),
                requestDTO.orderNumber(),
                requestDTO.totalAmount(),
                requestDTO.addressList(),
                requestDTO.items()

        );
    }
}
