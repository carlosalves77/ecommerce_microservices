package com.carldev.product_catalog_service.service;

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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }


    @Transactional
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> listOfAllProducts = productRepository.findAll();

        return listOfAllProducts.stream().map(productMapper::toDto
        ).collect(Collectors.toList());
    }


    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        if (productRepository.existsBySku(productRequestDTO.sku())) {
            throw new SkuAlreadyExistsException("O sku informado já existe");
        }

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

    @Transactional
    public ProductResponseDTO updateProduct(UpdateProductRequestDTO requestDTO, Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductIdNotExistsException("Produto não existente")
        );

        product.setName(requestDTO.name());
        product.setDescription(requestDTO.description());
        product.setPrice(requestDTO.price());
        product.setImageUrl(requestDTO.imageUrl());
        product.setActive(requestDTO.active());

        Product updateProduct = productRepository.save(product);

        return productMapper.toDto(updateProduct);
    }

    @Transactional
    public ProductResponseDTO findProductSkuByCriteria(String sku) {

        Product product = productRepository.findBySku(sku).orElseThrow(
                () -> new SkuAlreadyExistsException("Sku não existe"));

        return productMapper.toDto(product);
    }

    public void deleteProduct(Long id) {

        if (productRepository.findById(id).isEmpty()) {
            throw new ProductIdNotExistsException("Produto não existente");
        }

        productRepository.deleteById(id);
    }
}
