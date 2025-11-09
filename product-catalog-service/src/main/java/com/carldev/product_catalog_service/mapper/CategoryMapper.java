package com.carldev.product_catalog_service.mapper;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toDto(Category category);
}
