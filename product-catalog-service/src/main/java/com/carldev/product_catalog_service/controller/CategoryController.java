package com.carldev.product_catalog_service.controller;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import com.carldev.product_catalog_service.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CategoryResponseDTO>> listAllCategories(
            @RequestParam("page") int page

    ) {
        Page<CategoryResponseDTO> categoryResponseDTOList = categoryService.getAllCategories(page);

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

    @GetMapping("/slug")
    public ResponseEntity<List<CategoryResponseDTO>> findBySlug(
            @RequestParam("q") String slug
    ) {

        List<CategoryResponseDTO> responseDTO = categoryService.findBySlug(slug);

        return ResponseEntity.ok().body(responseDTO);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body("Slug deletado");
    }

}
