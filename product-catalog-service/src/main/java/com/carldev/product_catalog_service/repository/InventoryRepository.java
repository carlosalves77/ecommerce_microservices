package com.carldev.product_catalog_service.repository;

import com.carldev.product_catalog_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
