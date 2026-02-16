package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.exception.SlugAlreadyExistsException;
import com.carldev.product_catalog_service.mapper.CategoryMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<CategoryResponseDTO> getAllCategories(int page) {

        int pageNumber = page - 1;

        Pageable pageable = PageRequest.of(pageNumber, 10);

        Page<Category> allCategories = categoryRepository.findAll(pageable);

        return allCategories.map(categoryMapper::toDto);
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


    public List<CategoryResponseDTO> findBySlug(String slug) {

        if (slug.isBlank()) {
            throw new SlugAlreadyExistsException("Slug está vazio");
        }

        List<Category> categoryList = categoryRepository.findBySlugContainingIgnoreCase(slug);

        return categoryList.stream().map(categoryMapper::toDto).collect(
                Collectors.toList()
        );
    }

}
