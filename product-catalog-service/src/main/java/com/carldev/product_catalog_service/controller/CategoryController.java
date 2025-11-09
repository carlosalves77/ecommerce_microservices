package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> listAllCategories() {

        List<CategoryResponseDTO> categoryResponseDTOList = categoryService.getAllCategories();

        return ResponseEntity.ok().body(categoryResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid
            @RequestBody
            CategoryRequestDTO categoryRequestDTO) {

        CategoryResponseDTO categoryResponseDTO = categoryService.createCategory(categoryRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable() Long id,
            @Valid
            @RequestBody CategoryRequestDTO dto) {

        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(id, dto);

        return ResponseEntity.ok().body(categoryResponseDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body("Slug deletado");
    }

}
