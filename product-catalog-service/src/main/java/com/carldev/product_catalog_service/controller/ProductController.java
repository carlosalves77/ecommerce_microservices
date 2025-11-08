package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.UpdateProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.entity.Product;
import com.carldev.product_catalog_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> allProducts = productService.getAllProducts();
        return ResponseEntity.ok().body(allProducts);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {

        ProductResponseDTO newProductResponse = productService.createProduct(productRequestDTO);

        return new ResponseEntity<>(newProductResponse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @RequestParam("id") Long id, @RequestBody UpdateProductRequestDTO requestDTO
            ) {

        ProductResponseDTO productResponseDTO = productService.updateProduct(requestDTO, id);

        return ResponseEntity.ok().body(productResponseDTO);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteProduct(
            @RequestParam("id") Long id
    ) {
        productService.deleteProduct(id);
      return  ResponseEntity.ok().body("Produto deletado");
    }
}
