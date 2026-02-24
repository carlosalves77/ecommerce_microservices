package com.carldev.shopping_cart_service.feignClient;

import com.carldev.shopping_cart_service.config.FeignClientInterceptor;
import com.carldev.shopping_cart_service.dto.response.AddressResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://auth-service:4005", configuration = FeignClientInterceptor.class)
public interface AddressClient {

    @GetMapping("/api/v1/address/{id}")
    AddressResponseDTO findAddressById(@PathVariable("id") UUID id);

}
