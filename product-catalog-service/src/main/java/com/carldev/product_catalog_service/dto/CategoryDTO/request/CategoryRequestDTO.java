package com.carldev.product_catalog_service.dto.CategoryDTO.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Slug é obrigatório")
        String slug
) {
}
