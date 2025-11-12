package com.carldev.product_catalog_service.exception;

public class InvalidUpdateRequestException extends RuntimeException {
    public InvalidUpdateRequestException(String message) {
        super(message);
    }
}
