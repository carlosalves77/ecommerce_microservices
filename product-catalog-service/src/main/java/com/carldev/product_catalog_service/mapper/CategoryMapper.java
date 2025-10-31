package com.carldev.product_catalog_service.mapper;

import com.carldev.product_catalog_service.dto.ProductDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toDto(Category category);
}
