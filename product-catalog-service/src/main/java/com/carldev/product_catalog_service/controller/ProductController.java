package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {

        ProductResponseDTO newProductResponse = productService.createProduct(productRequestDTO);

        return new ResponseEntity<>(newProductResponse, HttpStatus.CREATED);
    }
}
