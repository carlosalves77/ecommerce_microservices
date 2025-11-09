package com.carldev.product_catalog_service.dto.CategoryDTO.response;

public record CategoryResponseDTO(
        Long id,
        String name,
        String slug
) {
}
