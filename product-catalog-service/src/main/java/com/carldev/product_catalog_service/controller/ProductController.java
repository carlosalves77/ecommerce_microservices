package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductDebitRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.UpdateProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponseDTO> getProductBySkuCriteria(
            @PathVariable String sku
    ) {
       ProductResponseDTO productResponseSku = productService.findProductSku(sku);

        return ResponseEntity.ok().body(productResponseSku);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> getProductByName(
            @RequestParam("q") String q
    ) {
        List<ProductResponseDTO> productList = productService.findProductByQuery(q);

        return ResponseEntity.ok().body(productList);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
           @RequestParam("page") int page
    ) {

        Page<ProductResponseDTO> allProducts = productService.getAllProductList(page);

        return ResponseEntity.status(HttpStatus.OK).body(allProducts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/payment/debit")
    public ResponseEntity<ProductResponseDTO> getDebitInventoryQuantity(
            @Valid
            @RequestBody
            ProductDebitRequestDTO productDebitRequestDTO
    ) {
        ProductResponseDTO productResponseDTO = productService.getProductDebit(productDebitRequestDTO);

        return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/payment/rollback")
    public ResponseEntity<ProductResponseDTO> getRollbackInventoryQuantity(
            @RequestBody
            ProductDebitRequestDTO productDebitRequestDTO
    ) {

        ProductResponseDTO product = productService.rollBackProduct(productDebitRequestDTO);

        return ResponseEntity.ok().body(product);
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
            @RequestParam("id") Long id,
            @Valid
            @RequestBody UpdateProductRequestDTO requestDTO
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
