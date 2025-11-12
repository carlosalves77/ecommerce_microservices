package com.carldev.product_catalog_service.dto.ProductDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequestDTO(
        String name,
        String description,
        @NotNull(message = "Informe o preço do produto (Opicional)")
        BigDecimal price,
        @NotBlank(message = "Informe o nome do produto (Opicional)")
        String imageUrl,
        @NotNull(message = "Informe se o produto está ativo")
        Boolean active
) {
}
