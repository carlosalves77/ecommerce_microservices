package com.carldev.order_service.repository;

import com.carldev.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> deleteByOrderNumber(Long orderNumber);

    Optional<Order> findByOrderNumber(Long orderNumber);

}
