package com.carldev.shopping_cart_service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String sku;
    private Integer quantity;
    private BigDecimal price;

}



