package com.carldev.shopping_cart_service.controller;

import com.carldev.shopping_cart_service.dto.request.AddItemRequestDTO;
import com.carldev.shopping_cart_service.dto.response.CartItemResponseDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ProductCatalogClient productCatalogClient;
    private final CartService cartService;

    public ShoppingCartController(ProductCatalogClient productCatalogClient,
                                  CartService cartService) {
        this.productCatalogClient = productCatalogClient;
        this.cartService = cartService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponseDTO> testFeign(@PathVariable String sku) {

        ProductResponseDTO product = productCatalogClient.getProductBySku(sku);

        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponseDTO> AddItemCart(
            Authentication authentication,
            @Valid
            @RequestBody AddItemRequestDTO addItemRequestDTO
    ) {

        CartItemResponseDTO responseDTO =  cartService.AddItemToCart(authentication, addItemRequestDTO);

        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/{sku}/{quantity}")
    public ResponseEntity<CartItemResponseDTO> updateItemCart(
            @Valid
            @PathVariable String sku,
            @PathVariable Integer quantity,
            Authentication authentication
    ) {

        CartItemResponseDTO responseDTO = cartService.UpdateQuantityCart(authentication, sku, quantity);

        return ResponseEntity.ok().body(responseDTO);
    }


    @GetMapping
    public ResponseEntity<Iterable<Cart>> listAllCartProducts() {
        Iterable<Cart> listAllProducts = cartService.getAllCartProducts();
        return ResponseEntity.ok().body(listAllProducts);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutProcess(Authentication authentication) {

        cartService.processCheckout(authentication);

        return ResponseEntity.ok().body("Produtos enviado para checkout pagamento");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCartItems() {
        cartService.deleteAllItemCart();
        return ResponseEntity.ok().body("Todos items do carrinho deletados");
    }


    @DeleteMapping("/{sku}")
    public ResponseEntity<String> deleteSingleItemFromCart(
            Authentication authentication, @PathVariable String sku) {

        cartService.deleteSingleItemCart(authentication, sku);

        return ResponseEntity.ok().body("Item excluido");
    }


}
