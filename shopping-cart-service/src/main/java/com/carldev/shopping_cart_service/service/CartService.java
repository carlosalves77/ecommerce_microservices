package com.carldev.shopping_cart_service.service;

import com.carldev.shopping_cart_service.dto.request.AddItemRequestDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.exception.HandleIfCartNotFoundException;
import com.carldev.shopping_cart_service.exception.HandleQuantityNotValidException;
import com.carldev.shopping_cart_service.exception.HandleSkuNotExistsException;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.redis.CartItem;
import com.carldev.shopping_cart_service.repository.CartRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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


    public void AddItemToCart(Authentication authentication, AddItemRequestDTO requestDTO) {


        ProductResponseDTO productResponseDTO;

        try {
            productResponseDTO = productCatalogClient.getProductBySku(requestDTO.sku());
        } catch (FeignException e) {
          throw new  HandleSkuNotExistsException("Sku não existente");
        }

        if (productResponseDTO.stockQuantity() < requestDTO.quantity()) {
            throw new HandleQuantityNotValidException("Estoque insuficiente!");
        }

         productCatalogClient.getProductDebit(requestDTO);


        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));

        Cart cart = cartRepository.findById(userId).orElse(new Cart());
        cart.setUserId(userId);

        Optional<CartItem> existingItem = cart.getItems().stream().filter(
                item -> item.getSku().equals(requestDTO.sku())).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + requestDTO.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setSku(requestDTO.sku());
            newItem.setQuantity(requestDTO.quantity());
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

        ProductResponseDTO productResponseDTO;

        try {
          productResponseDTO = productCatalogClient.getProductBySku(sku);
        } catch (FeignException e) {
          throw new  HandleIfCartNotFoundException("Carrinho não encontrado");
        }

        if (newQuantity <= 0) {
            throw new  HandleQuantityNotValidException("Não é permitido o valor");
        }

        if (productResponseDTO.stockQuantity() < newQuantity) {
            throw new RuntimeException("Estoque insuficiente para a quantidade desejada!");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));

        Cart cart = cartRepository.findById(userId).orElseThrow(
                () -> new HandleIfCartNotFoundException("Carrinho não encontrado")
        );

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getSku().equals(sku)).findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));

        cartItem.setQuantity(newQuantity);

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
