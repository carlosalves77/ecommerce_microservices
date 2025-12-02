package com.carldev.shopping_cart_service.exception;


public class HandleSkuNotExistsException extends RuntimeException{
    public HandleSkuNotExistsException(String message) {
        super(message);
    }
}
