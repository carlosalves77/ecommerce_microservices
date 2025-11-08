package com.carldev.product_catalog_service.exception;

public class ProductIdNotExistsException extends RuntimeException {

    public ProductIdNotExistsException(String message) {
        super(message);
    }
}
