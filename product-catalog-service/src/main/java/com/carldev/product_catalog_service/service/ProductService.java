package com.carldev.product_catalog_service.service;

import com.carldev.product_catalog_service.dto.ProductDTO.request.ProductRequestDTO;
import com.carldev.product_catalog_service.dto.ProductDTO.response.ProductResponseDTO;
import com.carldev.product_catalog_service.entity.Category;
import com.carldev.product_catalog_service.entity.Inventory;
import com.carldev.product_catalog_service.entity.Product;
import com.carldev.product_catalog_service.mapper.ProductMapper;
import com.carldev.product_catalog_service.repository.CategoryRepository;
import com.carldev.product_catalog_service.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        Product newProduct = productMapper.toEntity(productRequestDTO);
        newProduct.setActive(true);

        Set<Category> categories = new HashSet<>(
                categoryRepository.findAllById(productRequestDTO.categoryIds())
        );
        newProduct.setCategories(categories);

        Inventory newInventory = new Inventory();
        newInventory.setStockQuantity(productRequestDTO.stockQuantity());
        newInventory.setReservedQuantity(0);

        newProduct.setInventory(newInventory);
        newInventory.setProduct(newProduct);

        Product saveProduct = productRepository.save(newProduct);

        return productMapper.toDto(saveProduct);
    }
}
