package com.carldev.shopping_cart_service.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Cart", timeToLive = 3600)
public class Cart {

    @Id
    private UUID userId;

    private List<CartItem> items = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    public Optional<CartItem> findBySku(String sku) {
        return this.items.stream().filter(
                item -> item.getSku().equals(sku)
        ).findFirst();
    }

    public void recalculateTotal() {
        BigDecimal newTotal = BigDecimal.ZERO;

        for (CartItem item : this.items) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            newTotal = newTotal.add(lineTotal);
        }

     this.total = newTotal;

    }
}


