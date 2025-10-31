package com.carldev.product_catalog_service.dto.ProductDTO.response;

public record CategoryResponseDTO(
        Long id,
        String name,
        String slug
) {
}
