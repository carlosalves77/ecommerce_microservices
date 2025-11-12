package com.carldev.shopping_cart_service.repository;

import com.carldev.shopping_cart_service.redis.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


public interface CartRepository extends CrudRepository<Cart, UUID> {

}
