package com.carldev.product_catalog_service.dto.ProductDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record ProductRequestDTO(

        @NotBlank(message = "informe o número do SKU")
        String sku,

        @NotBlank(message = "Informe o nome do produto")
        String name,

        String description,

        @NotNull(message = "Informe o preço do produto")
        @Positive(message = "Preço deve ser maior que zero")
        BigDecimal price,

        String imageUrl,

        @NotNull(message = "Estoque inicial é obrigatório")
        Integer stockQuantity,

        @NotNull(message = "A lista de Ids de categoria é obrigatória")
        Set<Long> categoryIds
) {
}
