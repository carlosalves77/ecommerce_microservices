package com.carldev.shopping_cart_service.dto.response;

import com.carldev.shopping_cart_service.redis.CartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartItemResponseDTO(
        List<CartItem> items,
        BigDecimal total
) {
}
