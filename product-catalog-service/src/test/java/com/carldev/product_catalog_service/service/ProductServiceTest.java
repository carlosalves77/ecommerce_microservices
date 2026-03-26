package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.CategoryDTO.response.CategoryResponseDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductDebitRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.request.UpdateProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.entity.Inventory;
import com.carldev.product_catalog_service.entity.Product;
import com.carldev.product_catalog_service.exception.ProductIdNotExistsException;
import com.carldev.product_catalog_service.exception.SkuAlreadyExistsException;
import com.carldev.product_catalog_service.mapper.ProductMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import com.carldev.product_catalog_service.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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



    @Test
    @DisplayName("Deve criar produto com sucesso")
    void shouldCreateProductSuccessfully() {

        BigDecimal price = BigDecimal.valueOf(300.00);
        Set<Long> categoryId = new HashSet<>();
        categoryId.add(1L);

        Set<CategoryResponseDTO> categoryResponseDTOS = Collections.singleton(new CategoryResponseDTO(
                1L, "Livros e Cultura", "livros-e-cultura"
        ));

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "BRQ-LEGO-SW", "Lego Star Wars",
                "Brinquedo do Star Wars Action", price, "", 30, categoryId, Map.of()
        );

        Product productEntity = new Product();
        Category category = new Category();
        category.setId(1L);

        Product savedProduct = new Product();
        savedProduct.setId(100L);
        savedProduct.setSku("BRQ-LEGO-SW");

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "100", "BRQ-LEGO-SW", "Lego Star Wars",
                "Brinquedo do Star Wars Action", price, "", true,
                LocalDateTime.now(), LocalDateTime.now(), 30, 0,
                categoryResponseDTOS, Map.of()
        );

        when(productRepository.existsBySku(productRequestDTO.sku())).thenReturn(false);
        when(productMapper.toEntity(productRequestDTO)).thenReturn(productEntity);
        when(categoryRepository.findAllById(productRequestDTO.categoryIds())).thenReturn(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("BRQ-LEGO-SW", result.sku());
        assertNotNull(productEntity.getInventory());
        assertEquals(30, productEntity.getInventory().getStockQuantity());
        assertTrue(productEntity.isActive());
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU já existir")
    void shouldThrowExceptionWhenSkuAlreadyExists() {

        Set<Long> categoryId = new HashSet<>();
        categoryId.add(1L);

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "BRQ-LEGO-SW", "Lego Star Wars",
                "Brinquedo do Star Wars Action", BigDecimal.valueOf(300), "",
                30, categoryId, Map.of()
        );

        when(productRepository.existsBySku(productRequestDTO.sku())).thenReturn(true);

        SkuAlreadyExistsException exception = assertThrows(SkuAlreadyExistsException.class,
                () -> productService.createProduct(productRequestDTO));

        assertEquals("O sku informado já existe", exception.getMessage());
        verify(productRepository, never()).save(any());
        verifyNoInteractions(categoryRepository);
    }


    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void shouldUpdateProductSuccessfully() {

        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setSku("SKU-001");

        UpdateProductRequestDTO updateDTO = new UpdateProductRequestDTO(
                "Produto Atualizado", "Descrição nova",
                BigDecimal.valueOf(250), "http://img.com/new.jpg", true
        );

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "1", "SKU-001", "Produto Atualizado", "Descrição nova",
                BigDecimal.valueOf(250), "http://img.com/new.jpg", true,
                LocalDateTime.now(), LocalDateTime.now(), 10, 0,
                Set.of(), Map.of()
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.updateProduct(updateDTO, productId);

        assertNotNull(result);
        assertEquals("Produto Atualizado", result.name());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao atualizar")
    void shouldThrowWhenProductNotFoundOnUpdate() {

        Long productId = 999L;
        UpdateProductRequestDTO updateDTO = new UpdateProductRequestDTO(
                "Nome", "Desc", BigDecimal.TEN, "url", true
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductIdNotExistsException.class,
                () -> productService.updateProduct(updateDTO, productId));
    }


    @Test
    @DisplayName("Deve debitar estoque do produto")
    void shouldDebitProductStock() {

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(50);
        inventory.setReservedQuantity(0);
        inventory.setProduct(product);
        product.setInventory(inventory);

        ProductDebitRequestDTO debitDTO = new ProductDebitRequestDTO("SKU-001", 5);

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "1", "SKU-001", "Product", "Desc",
                BigDecimal.TEN, "", true, LocalDateTime.now(),
                LocalDateTime.now(), 45, 0, Set.of(), Map.of()
        );

        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductDebit(debitDTO);

        assertNotNull(result);
        assertEquals(45, product.getInventory().getStockQuantity());
        verify(productRepository, times(1)).save(product);
    }



    @Test
    @DisplayName("Deve fazer rollback do estoque do produto")
    void shouldRollBackProductStock() {

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(45);
        inventory.setReservedQuantity(5);
        inventory.setProduct(product);
        product.setInventory(inventory);

        ProductDebitRequestDTO rollbackDTO = new ProductDebitRequestDTO("SKU-001", 5);

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "1", "SKU-001", "Product", "Desc",
                BigDecimal.TEN, "", true, LocalDateTime.now(),
                LocalDateTime.now(), 50, 0, Set.of(), Map.of()
        );

        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.rollBackProduct(rollbackDTO);

        assertNotNull(result);
        assertEquals(50, product.getInventory().getStockQuantity());
        assertEquals(0, product.getInventory().getReservedQuantity());
    }



    @Test
    @DisplayName("Deve encontrar produto pelo SKU")
    void shouldFindProductBySku() {

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "1", "SKU-001", "Product", "Desc",
                BigDecimal.TEN, "", true, LocalDateTime.now(),
                LocalDateTime.now(), 10, 0, Set.of(), Map.of()
        );

        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.findProductSku("SKU-001");

        assertNotNull(result);
        assertEquals("SKU-001", result.sku());
    }



    @Test
    @DisplayName("Deve deletar produto pelo ID")
    void shouldDeleteProduct() {

        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao deletar")
    void shouldThrowWhenProductNotFoundOnDelete() {

        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductIdNotExistsException.class,
                () -> productService.deleteProduct(productId));

        verify(productRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("Deve retornar página de produtos")
    void shouldGetAllProductList() {

        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");

        ProductResponseDTO dto = new ProductResponseDTO(
                "1", "SKU-001", "Product", "Desc",
                BigDecimal.TEN, "", true, LocalDateTime.now(),
                LocalDateTime.now(), 10, 0, Set.of(), Map.of()
        );

        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(productMapper.toDto(product)).thenReturn(dto);

        Page<ProductResponseDTO> result = productService.getAllProductList(1);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }


    @Test
    @DisplayName("Deve retornar produto pelo ID")
    void shouldGetProductById() {

        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setSku("SKU-001");

        ProductResponseDTO responseDTO = new ProductResponseDTO(
                "1", "SKU-001", "Product", "Desc",
                BigDecimal.TEN, "", true, LocalDateTime.now(),
                LocalDateTime.now(), 10, 0, Set.of(), Map.of()
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals("SKU-001", result.sku());
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado pelo ID")
    void shouldThrowWhenProductNotFoundById() {

        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductIdNotExistsException.class,
                () -> productService.getProductById(productId));
    }
}