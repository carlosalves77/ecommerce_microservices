package com.carldev.shopping_cart_service.service;

import com.carldev.shopping_cart_service.dto.OrderItemDTO;
import com.carldev.shopping_cart_service.dto.request.OrderPlacementRequestDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.redis.CartItem;
import com.carldev.shopping_cart_service.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductCatalogClient productCatalogClient;


    public CartService(CartRepository cartRepository, ProductCatalogClient productCatalogClient) {
        this.cartRepository = cartRepository;
        this.productCatalogClient = productCatalogClient;
    }


    public void AddItemToCart(Authentication authentication, String sku, int quantity) {

        ProductResponseDTO productResponseDTO = productCatalogClient.getProductBySku(sku);

        if (productResponseDTO.stockQuantity() < quantity) {
            throw new RuntimeException("Estoque insuficiente!");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));


        Cart cart = cartRepository.findById(userId).orElse(new Cart());
        cart.setUserId(userId);

        Optional<CartItem> existingItem = cart.getItems().stream().filter(
                item -> item.getSku().equals(sku)).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setSku(sku);
            newItem.setQuantity(quantity);
            newItem.setPrice(productResponseDTO.price());
            cart.getItems().add(newItem);
        }

        cart.recalculateTotal();

        cartRepository.save(cart);
    }

    public List<Cart> getAllCartProducts() {

        Iterable<Cart> listAllProducts = cartRepository.findAll();

        return StreamSupport.stream(listAllProducts.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void UpdateQuantityCart(Authentication authentication, String sku, Integer newQuantity) {

        ProductResponseDTO productResponseDTO = productCatalogClient.getProductBySku(sku);
        if (productResponseDTO.stockQuantity() < newQuantity) {
            throw new RuntimeException("Estoque insuficiente para a quantidade desejada!");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));

        Cart cart = cartRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Carrinho não encontrado")
        );

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getSku().equals(sku)).findFirst()
                        .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));

        if(newQuantity > 0) {
            cartItem.setQuantity(newQuantity);
        } else {
            cart.getItems().remove(cartItem);
        }

        cart.recalculateTotal();

        cartRepository.save(cart);
    }

    public void deleteAllItemCart() {
        cartRepository.deleteAll();
    }

    public void deleteSingleItemCart(Authentication authentication, String sku) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));


        Cart cart = cartRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("Id de usuário não existe")
        );

        Optional<CartItem> itemToRemove = cart.findBySku(sku);

        if (itemToRemove.isPresent()) {
            cart.getItems().remove(itemToRemove.get());
            cartRepository.save(cart);
        } else {
            throw new RuntimeException("Item com o SKU informado não encontrado");
        }

    }
}
