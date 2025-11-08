package com.carldev.product_catalog_service.mapper;

import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(source = "inventory.stockQuantity", target = "stockQuantity")
    @Mapping(source = "inventory.reservedQuantity", target = "reservedQuantity")
    ProductResponseDTO toDto(Product product);

    Product toEntity(ProductRequestDTO dto);

}
