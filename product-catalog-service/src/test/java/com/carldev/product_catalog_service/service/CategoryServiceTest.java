package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.CategoryDTO.request.CategoryRequestDTO;
import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.exception.SlugAlreadyExistsException;
import com.carldev.product_catalog_service.mapper.CategoryMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;


    @Test
    @DisplayName("Deve retornar página de categorias")
    void shouldGetAllCategories() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Eletrônicos");
        category.setSlug("eletronicos");

        CategoryResponseDTO dto = new CategoryResponseDTO(1L, "Eletrônicos", "eletronicos");

        Page<Category> page = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(categoryMapper.toDto(category)).thenReturn(dto);

        Page<CategoryResponseDTO> result = categoryService.getAllCategories(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Eletrônicos", result.getContent().get(0).name());
    }


    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void shouldCreateCategory() {

        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Livros", "livros");

        Category category = new Category();
        category.setName("Livros");
        category.setSlug("livros");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Livros");
        savedCategory.setSlug("livros");

        CategoryResponseDTO responseDTO = new CategoryResponseDTO(1L, "Livros", "livros");

        when(categoryRepository.existsBySlug("livros")).thenReturn(false);
        when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Livros", result.name());
        assertEquals("livros", result.slug());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Deve lançar exceção quando slug já existir ao criar categoria")
    void shouldThrowWhenSlugAlreadyExistsOnCreate() {

        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Livros", "livros");

        when(categoryRepository.existsBySlug("livros")).thenReturn(true);

        assertThrows(SlugAlreadyExistsException.class,
                () -> categoryService.createCategory(requestDTO));

        verify(categoryRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void shouldUpdateCategory() {

        Long categoryId = 1L;
        CategoryRequestDTO dto = new CategoryRequestDTO("Livros Atualizados", "livros-atualizados");

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Livros");
        category.setSlug("livros");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Livros Atualizados");
        updatedCategory.setSlug("livros-atualizados");

        CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                categoryId, "Livros Atualizados", "livros-atualizados"
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsBySlug("livros-atualizados")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.updateCategory(categoryId, dto);

        assertNotNull(result);
        assertEquals("Livros Atualizados", result.name());
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada ao atualizar")
    void shouldThrowWhenCategoryNotFoundOnUpdate() {

        Long categoryId = 999L;
        CategoryRequestDTO dto = new CategoryRequestDTO("Test", "test");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(SlugAlreadyExistsException.class,
                () -> categoryService.updateCategory(categoryId, dto));
    }


    @Test
    @DisplayName("Deve deletar categoria pelo ID")
    void shouldDeleteCategory() {

        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada ao deletar")
    void shouldThrowWhenCategoryNotFoundOnDelete() {

        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(SlugAlreadyExistsException.class,
                () -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("Deve encontrar categorias pelo slug")
    void shouldFindBySlug() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Eletrônicos");
        category.setSlug("eletronicos");

        CategoryResponseDTO dto = new CategoryResponseDTO(1L, "Eletrônicos", "eletronicos");

        when(categoryRepository.findBySlugContainingIgnoreCase("eletr"))
                .thenReturn(List.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        List<CategoryResponseDTO> result = categoryService.findBySlug("eletr");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Eletrônicos", result.get(0).name());
    }

    @Test
    @DisplayName("Deve lançar exceção quando slug estiver vazio na busca")
    void shouldThrowWhenSlugIsBlank() {

        assertThrows(SlugAlreadyExistsException.class,
                () -> categoryService.findBySlug("  "));
    }
}
