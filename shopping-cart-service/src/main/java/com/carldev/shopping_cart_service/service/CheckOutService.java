package com.carldev.shopping_cart_service.service;

import com.carldev.shopping_cart_service.dto.OrderItemDTO;
import com.carldev.shopping_cart_service.dto.request.OrderPlacementRequestDTO;
import com.carldev.shopping_cart_service.kafka.CheckOutCreatedEvent;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CheckOutService {

    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;


    public CheckOutService(CartRepository cartRepository, ApplicationEventPublisher
            eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;

    }

    public void processCheckout(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));

        Cart cart = cartRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Carrinho não encontrado")
        );

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("O carrinho está vazio");
        }

        OrderPlacementRequestDTO requestDTO = mapToOrder(cart, userId);

        CheckOutCreatedEvent event = CheckOutCreatedEvent.fromEntity(requestDTO);

        eventPublisher.publishEvent(event);


        cart.getItems().clear();
        cart.recalculateTotal();
        cartRepository.save(cart);

    }


    private OrderPlacementRequestDTO mapToOrder(Cart cart, UUID uuid) {

        List<OrderItemDTO> orderItemDTOList = cart.getItems().stream()
                .map(items -> new OrderItemDTO(
                        items.getSku(),
                        items.getQuantity(),
                        items.getPrice()
                )).toList();

        return new OrderPlacementRequestDTO(
                uuid,
                cart.getTotal(),
                orderItemDTOList
        );
    }

}
