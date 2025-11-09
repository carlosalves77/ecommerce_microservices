package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.exception.SlugAlreadyExistsException;
import com.carldev.product_catalog_service.mapper.CategoryMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponseDTO> getAllCategories() {

        List<Category> allCategories = categoryRepository.findAll();

        return allCategories.stream().map(categoryMapper::toDto
        ).collect(Collectors.toList());
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {

        if (categoryRepository.existsBySlug(categoryRequestDTO.slug())) {
            throw new SlugAlreadyExistsException("Slug já existe");
        }

        Category addNewCategory = categoryMapper.toEntity(categoryRequestDTO);

        Category responseCategory = categoryRepository.save(addNewCategory);

        return categoryMapper.toDto(responseCategory);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {

       Category category = categoryRepository.findById(id).orElseThrow(
                () -> new SlugAlreadyExistsException("Slug não existe")
        );

        if (categoryRepository.existsBySlug(dto.slug())) {
            throw new SlugAlreadyExistsException("Slug já existe");
        }

        category.setName(dto.name());
        category.setSlug(dto.slug());

        Category updateCategory = categoryRepository.save(category);

        return categoryMapper.toDto(updateCategory);
    }

    public void deleteCategory(Long id) {

        categoryRepository.findById(id).orElseThrow(
                () -> new SlugAlreadyExistsException("Slug não existe")
        );

        categoryRepository.deleteById(id);
    }


}
