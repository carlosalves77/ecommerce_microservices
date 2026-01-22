package com.carldev.order_service.feignClient;

import com.carldev.order_service.dto.request.AddItemRequestDTO;
import com.carldev.order_service.dto.request.RollbackItemRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-catalog-payment", url = "http://product-catalog-service:4004")
public interface ProductCatalogClient {

    @PostMapping("/api/product/payment/debit")
    void getProductDebit(@RequestBody AddItemRequestDTO dto);


    // TODO Implementar rollBack
    @PostMapping("/api/product/payment/rollback")
    void getProductRollback(@RequestBody RollbackItemRequestDTO dto);

}
