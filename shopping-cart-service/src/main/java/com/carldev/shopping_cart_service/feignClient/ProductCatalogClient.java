package com.carldev.shopping_cart_service.feignClient;

import com.carldev.shopping_cart_service.dto.response.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "product-catalog", url = "http://product-catalog-service:4004")
public interface ProductCatalogClient {

    @GetMapping("/api/product/{sku}")
    ProductResponseDTO getProductBySku(@PathVariable("sku") String sku);

    // TODO - Corrigir debido do produto ao efetuar o pagamento
//    @PostMapping("/api/product/payment/debit")
//    AddItemRequestDTO getProductDebit(@RequestBody  AddItemRequestDTO dto);
//
//    TODO - Corrigir rollback produtos ao cancelar/erro pagamento
//    @PostMapping("/api/product/payment/rollback")
//    RollbackItemRequestDTO getProductDebit(@RequestBody RollbackItemRequestDTO dto);

}
