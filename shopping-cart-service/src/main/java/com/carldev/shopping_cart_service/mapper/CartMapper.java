package com.carldev.shopping_cart_service.mapper;


import com.carldev.shopping_cart_service.dto.response.CartItemResponseDTO;
import com.carldev.shopping_cart_service.redis.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItemResponseDTO toDto(Cart cart);
}
