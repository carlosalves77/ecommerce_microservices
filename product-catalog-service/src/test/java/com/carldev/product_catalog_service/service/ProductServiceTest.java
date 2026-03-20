package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.entity.Product;
import com.carldev.product_catalog_service.exception.SkuAlreadyExistsException;
import com.carldev.product_catalog_service.mapper.ProductMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import com.carldev.product_catalog_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

//    @Test
//    void ShouldReturnCreateProductSuccessfully() {
//
//
//        BigDecimal price = BigDecimal.valueOf(300.00);
//        Set<Long> categoryId = new HashSet<>();
//        categoryId.add(1L);
//
//
//        Set<CategoryResponseDTO> categoryResponseDTOS = Collections.singleton(new CategoryResponseDTO(
//                1L,
//                "Livros e Cultura",
//                "livros e cultura"
//        ));
//
//
//        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
//                "BRQ-LEGO-SW",
//                "Lego Star Wars",
//                "Brinquedo do Star Wars Action",
//                price,
//                "",
//                30,
//                categoryId
//        );
//
//        Product productEntity = new Product();
//        Category category = new Category();
//        category.setId(1L);
//
//        Product savedProduct = new Product();
//        savedProduct.setId(100L);
//        savedProduct.setSku("BRQ-LEGO-SW");
//
//
//        ProductResponseDTO responseDTO = new ProductResponseDTO(
//                "1",
//                "BRQ-LEGO-SW",
//                "Lego Star Wars",
//                "Brinquedo do Star Wars Action",
//                price,
//                "",
//                true,
//                LocalDateTime.now(),
//                LocalDateTime.now(),
//                10,
//                20,
//                categoryResponseDTOS
//        );
//
//        when(productRepository.existsBySku(productRequestDTO.sku())).thenReturn(false);
//        when(productMapper.toEntity(productRequestDTO)).thenReturn(productEntity);
//        when(categoryRepository.findAllById(productRequestDTO.categoryIds())).thenReturn(List.of(category));
//        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
//        when(productMapper.toDto(savedProduct)).thenReturn(responseDTO);
//
//        ProductResponseDTO result = productService.createProduct(productRequestDTO);
//
//        assertNotNull(result);
//        assertEquals(1L, Integer.parseInt(result.id()));
//        assertEquals("BRQ-LEGO-SW", result.sku());
//
//        assertNotNull(productEntity.getInventory());
//        assertEquals(30, productEntity.getInventory().getStockQuantity());
//        assertEquals(productEntity, productEntity.getInventory().getProduct());
//        assertTrue(productEntity.isActive());
//
//        verify(productRepository, times(1)).save(productEntity);
//    }
//
//    @Test
//    void ShouldThrowExceptionWhenSkuAlreadyExists() {
//
//
//        BigDecimal price = BigDecimal.valueOf(300.00);
//        Set<Long> categoryId = new HashSet<>();
//        categoryId.add(1L);
//
//
//        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
//                "BRQ-LEGO-SW",
//                "Lego Star Wars",
//                "Brinquedo do Star Wars Action",
//                price,
//                "",
//                30,
//                categoryId
//        );
//
//        when(productRepository.existsBySku(productRequestDTO.sku())).thenReturn(true);
//
//        SkuAlreadyExistsException existsException = assertThrows(SkuAlreadyExistsException.class,
//        () -> {productService.createProduct(productRequestDTO);} );
//
//        assertEquals("O sku informado já existe", existsException.getMessage());
//
//
//        verify(productRepository, never()).save(any());
//
//        verifyNoInteractions(categoryRepository);
//        verifyNoInteractions(productMapper);
//    }

}