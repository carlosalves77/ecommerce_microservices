package com.carldev.shopping_cart_service.dto.response;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String sku,
        String name,
        String image,
        BigDecimal price,
        Integer stockQuantity
) {
}
