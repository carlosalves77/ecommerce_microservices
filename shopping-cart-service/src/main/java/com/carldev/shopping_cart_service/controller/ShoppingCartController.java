package com.carldev.shopping_cart_service.controller;

import com.carldev.shopping_cart_service.dto.request.AddItemRequestDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.service.CartService;
import com.carldev.shopping_cart_service.service.CheckOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ProductCatalogClient productCatalogClient;
    private final CartService cartService;
    private final CheckOutService checkOutService;

    public ShoppingCartController(ProductCatalogClient productCatalogClient,
                                  CartService cartService,
                                  CheckOutService checkOutService) {
        this.productCatalogClient = productCatalogClient;
        this.cartService = cartService;
        this.checkOutService = checkOutService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponseDTO> testFeign(@PathVariable String sku) {

        ProductResponseDTO product = productCatalogClient.getProductBySku(sku);

        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/add")
    public ResponseEntity<String> AddItemCart(
            Authentication authentication,
            @RequestBody AddItemRequestDTO addItemRequestDTO
    ) {

        cartService.AddItemToCart(authentication, addItemRequestDTO.sku(),
                addItemRequestDTO.quantity());

        return ResponseEntity.ok().body("Item adicionado");
    }

    @PutMapping("/{sku}/{quantity}")
    public ResponseEntity<String> updateItemCart(
            @PathVariable String sku,
            @PathVariable Integer quantity,
            Authentication authentication
    ) {

        cartService.UpdateQuantityCart(authentication, sku, quantity);
        return ResponseEntity.ok().body("Sku atualizado: " + sku);
    }


    @GetMapping
    public ResponseEntity<Iterable<Cart>> listAllCartProducts() {
        Iterable<Cart> listAllProducts = cartService.getAllCartProducts();
        return ResponseEntity.ok().body(listAllProducts);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutProcess(Authentication authentication) {
        checkOutService.processCheckout(authentication);
        return ResponseEntity.ok().body("Produtos enviados");
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
