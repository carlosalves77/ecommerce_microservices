package com.carldev.product_catalog_service.dto.ProductDTO.response;

import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public record ProductResponseDTO  (
        String id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        boolean active,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdAt,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime updatedAt,

        int stockQuantity,
        int reservedQuantity,

        Set<CategoryResponseDTO> categories,

        Map<String, Object>specifications
) implements Serializable {
}


