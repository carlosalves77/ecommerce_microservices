package com.carldev.product_catalog_service.dto.ProductDTO.response;

import com.carldev.product_catalog_service.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record ProductResponseDTO(
        String sku,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        int stockQuantity,
        int reservedQuantity,

        Set<CategoryResponseDTO> categories


) {
}


