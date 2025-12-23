package com.carldev.product_catalog_service.repository;

import com.carldev.product_catalog_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsBySlug(String slug);

    List<Category> findBySlugContainingIgnoreCase(String slug);

}
