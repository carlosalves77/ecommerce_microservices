package com.carldev.product_catalog_service.repository;

import com.carldev.product_catalog_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findAllByCategoriesSlug(String slug, Pageable pageable);

}
