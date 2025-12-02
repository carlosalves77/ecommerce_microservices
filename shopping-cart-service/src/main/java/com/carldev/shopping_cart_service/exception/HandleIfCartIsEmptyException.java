package com.carldev.shopping_cart_service.exception;

public class HandleIfCartIsEmptyException extends RuntimeException {

    public HandleIfCartIsEmptyException(String message) {
        super(message);
    }
}
