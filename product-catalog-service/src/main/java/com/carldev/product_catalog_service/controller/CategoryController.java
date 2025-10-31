package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.ProductDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @Valid
            @RequestBody
            CategoryRequestDTO categoryRequestDTO) {
        Category createCategory = categoryService.createCategory(categoryRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createCategory);
    }
}
