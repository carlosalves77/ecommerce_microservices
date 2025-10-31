package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.ProductDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

        private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {

        Category newCategory = new Category();
        newCategory.setName(categoryRequestDTO.name());
        newCategory.setSlug(categoryRequestDTO.slug());

        return categoryRepository.save(newCategory);
    }


}
