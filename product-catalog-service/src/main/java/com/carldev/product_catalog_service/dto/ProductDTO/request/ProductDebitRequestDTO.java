package com.carldev.product_catalog_service.dto.ProductDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDebitRequestDTO(
        @NotBlank(message = "Informe o sku")
        String sku,
        @NotNull(message = "Informe a quantidade")
        int quantity
) {
}
