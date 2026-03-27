package com.carldev.shopping_cart_service.service;

import com.carldev.shopping_cart_service.dto.request.AddItemRequestDTO;
import com.carldev.shopping_cart_service.dto.response.CartItemResponseDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.exception.HandleQuantityNotValidException;
import com.carldev.shopping_cart_service.exception.HandleSkuNotExistsException;
import com.carldev.shopping_cart_service.feignClient.AddressClient;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.mapper.CartMapper;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.redis.CartItem;
import com.carldev.shopping_cart_service.repository.CartRepository;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductCatalogClient productCatalogClient;

    @Mock
    private AddressClient addressClient;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CartMapper cartMapper;


    private Authentication createMockAuthentication(UUID userId) {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("userId", userId.toString())
                .claim("userName", "Carlos")
                .subject("carlos@example.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);
        return auth;
    }


    @Test
    @DisplayName("Deve adicionar novo item ao carrinho")
    void shouldAddNewItemToCart() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        AddItemRequestDTO requestDTO = new AddItemRequestDTO("SKU-001", 2);

        ProductResponseDTO productDTO = new ProductResponseDTO(
                1L, "SKU-001", "Produto Test", "img.jpg",
                BigDecimal.valueOf(100), 50
        );

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        CartItemResponseDTO responseDTO = new CartItemResponseDTO(
                List.of(new CartItem("SKU-001", 2, BigDecimal.valueOf(100))),
                BigDecimal.valueOf(200)
        );

        when(productCatalogClient.getProductBySku("SKU-001")).thenReturn(productDTO);
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(responseDTO);

        CartItemResponseDTO result = cartService.AddItemToCart(auth, requestDTO);

        assertNotNull(result);
        assertEquals(1, cart.getItems().size());
        assertEquals("SKU-001", cart.getItems().get(0).getSku());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Deve incrementar quantidade quando item já existe no carrinho")
    void shouldIncrementQuantityWhenItemExists() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        AddItemRequestDTO requestDTO = new AddItemRequestDTO("SKU-001", 3);

        ProductResponseDTO productDTO = new ProductResponseDTO(
                1L, "SKU-001", "Produto Test", "img.jpg",
                BigDecimal.valueOf(100), 50
        );

        CartItem existingItem = new CartItem("SKU-001", 2, BigDecimal.valueOf(100));
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        CartItemResponseDTO responseDTO = new CartItemResponseDTO(
                List.of(new CartItem("SKU-001", 5, BigDecimal.valueOf(100))),
                BigDecimal.valueOf(500)
        );

        when(productCatalogClient.getProductBySku("SKU-001")).thenReturn(productDTO);
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(responseDTO);

        CartItemResponseDTO result = cartService.AddItemToCart(auth, requestDTO);

        assertNotNull(result);
        assertEquals(5, existingItem.getQuantity());
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU não existir")
    void shouldThrowWhenSkuNotExists() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        AddItemRequestDTO requestDTO = new AddItemRequestDTO("SKU-INVALID", 1);

        when(productCatalogClient.getProductBySku("SKU-INVALID"))
                .thenThrow(mock(FeignException.class));

        assertThrows(HandleSkuNotExistsException.class,
                () -> cartService.AddItemToCart(auth, requestDTO));

        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void shouldThrowWhenStockInsufficient() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        AddItemRequestDTO requestDTO = new AddItemRequestDTO("SKU-001", 100);

        ProductResponseDTO productDTO = new ProductResponseDTO(
                1L, "SKU-001", "Produto", "img.jpg",
                BigDecimal.valueOf(100), 5
        );

        when(productCatalogClient.getProductBySku("SKU-001")).thenReturn(productDTO);

        assertThrows(HandleQuantityNotValidException.class,
                () -> cartService.AddItemToCart(auth, requestDTO));

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve retornar todos os carrinhos")
    void shouldGetAllCartProducts() {

        Cart cart1 = new Cart();
        cart1.setUserId(UUID.randomUUID());
        cart1.setItems(new ArrayList<>());

        Cart cart2 = new Cart();
        cart2.setUserId(UUID.randomUUID());
        cart2.setItems(new ArrayList<>());

        when(cartRepository.findAll()).thenReturn(List.of(cart1, cart2));

        List<Cart> result = cartService.getAllCartProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    @DisplayName("Deve atualizar quantidade do item no carrinho")
    void shouldUpdateQuantityInCart() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        ProductResponseDTO productDTO = new ProductResponseDTO(
                1L, "SKU-001", "Produto", "img.jpg",
                BigDecimal.valueOf(100), 50
        );

        CartItem cartItem = new CartItem("SKU-001", 2, BigDecimal.valueOf(100));
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        CartItemResponseDTO responseDTO = new CartItemResponseDTO(
                List.of(new CartItem("SKU-001", 5, BigDecimal.valueOf(100))),
                BigDecimal.valueOf(500)
        );

        when(productCatalogClient.getProductBySku("SKU-001")).thenReturn(productDTO);
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDto(cart)).thenReturn(responseDTO);

        CartItemResponseDTO result = cartService.UpdateQuantityCart(auth, "SKU-001", 5);

        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero ou negativa")
    void shouldThrowWhenQuantityIsZeroOrNegative() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        ProductResponseDTO productDTO = new ProductResponseDTO(
                1L, "SKU-001", "Produto", "img.jpg",
                BigDecimal.valueOf(100), 50
        );

        when(productCatalogClient.getProductBySku("SKU-001")).thenReturn(productDTO);

        assertThrows(HandleQuantityNotValidException.class,
                () -> cartService.UpdateQuantityCart(auth, "SKU-001", 0));

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve deletar item do carrinho pelo SKU")
    void shouldDeleteSingleItemFromCart() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        CartItem cartItem = new CartItem("SKU-001", 2, BigDecimal.valueOf(100));
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        cartService.deleteSingleItemCart(auth, "SKU-001");

        assertEquals(0, cart.getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU não encontrado no carrinho ao deletar")
    void shouldThrowWhenSkuNotFoundInCartOnDelete() {

        UUID userId = UUID.randomUUID();
        Authentication auth = createMockAuthentication(userId);

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        assertThrows(RuntimeException.class,
                () -> cartService.deleteSingleItemCart(auth, "SKU-INEXISTENTE"));
    }


    @Test
    @DisplayName("Deve deletar todos os carrinhos")
    void shouldDeleteAllItemsFromCart() {

        cartService.deleteAllItemCart();

        verify(cartRepository, times(1)).deleteAll();
    }
}
