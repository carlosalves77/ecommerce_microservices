package com.carldev.shopping_cart_service.controller;

import com.carldev.shopping_cart_service.dto.request.AddItemRequestDTO;
import com.carldev.shopping_cart_service.dto.response.AddressResponseDTO;
import com.carldev.shopping_cart_service.dto.response.CartItemResponseDTO;
import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import com.carldev.shopping_cart_service.feignClient.AddressClient;
import com.carldev.shopping_cart_service.feignClient.ProductCatalogClient;
import com.carldev.shopping_cart_service.redis.Cart;
import com.carldev.shopping_cart_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class ShoppingCartController {

    private final ProductCatalogClient productCatalogClient;
    private final AddressClient addressClient;
    private final CartService cartService;

    public ShoppingCartController(ProductCatalogClient productCatalogClient, AddressClient addressClient,
                                  CartService cartService) {
        this.productCatalogClient = productCatalogClient;
        this.addressClient = addressClient;
        this.cartService = cartService;
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponseDTO> testFeign(@PathVariable String sku) {

        ProductResponseDTO product = productCatalogClient.getProductBySku(sku);

        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> addressFeignClient(@PathVariable UUID id) {

        AddressResponseDTO address = addressClient.findAddressById(id);

        return ResponseEntity.status(HttpStatus.OK).body(address);
    }



    @PostMapping("/add")
    public ResponseEntity<CartItemResponseDTO> addItemCart(
            Authentication authentication,
            @Valid
            @RequestBody AddItemRequestDTO addItemRequestDTO
    ) {

        CartItemResponseDTO responseDTO =  cartService.AddItemToCart(authentication, addItemRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{sku}/{quantity}")
    public ResponseEntity<CartItemResponseDTO> updateItemCart(
            @Valid
            @PathVariable String sku,
            @PathVariable Integer quantity,
            Authentication authentication
    ) {

        CartItemResponseDTO responseDTO = cartService.UpdateQuantityCart(authentication, sku, quantity);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }


    @GetMapping
    public ResponseEntity<Iterable<Cart>> listAllCartProducts() {
        Iterable<Cart> listAllProducts = cartService.getAllCartProducts();
        return ResponseEntity.status(HttpStatus.OK).body(listAllProducts);
    }

    @PostMapping("/checkout/{address}")
    public ResponseEntity<String> checkoutProcess(
            @PathVariable("address") UUID address,
            Authentication authentication) {

        cartService.processCheckout(address, authentication);

        return ResponseEntity.status(HttpStatus.OK).body("Produtos enviado para checkout pagamento");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCartItems() {
        cartService.deleteAllItemCart();
        return ResponseEntity.status(HttpStatus.OK).body("Todos items do carrinho deletados");
    }


    @DeleteMapping("/{sku}")
    public ResponseEntity<String> deleteSingleItemFromCart(
            Authentication authentication, @PathVariable String sku) {

        cartService.deleteSingleItemCart(authentication, sku);

        return  ResponseEntity.status(HttpStatus.OK).body("Item excluido");
    }


}
