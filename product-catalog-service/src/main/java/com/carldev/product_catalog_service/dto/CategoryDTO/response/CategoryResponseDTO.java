package com.carldev.product_catalog_service.dto.CategoryDTO.response;

import java.io.Serializable;

public record CategoryResponseDTO (
        Long id,
        String name,
        String slug
) implements Serializable {
}
