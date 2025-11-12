package com.carldev.product_catalog_service.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String message) {
        super(message);
    }
}

