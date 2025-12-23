package com.carldev.shopping_cart_service.exception;

public class HandleIfCartNotFoundException extends RuntimeException {

    public HandleIfCartNotFoundException(String message) {
        super(message);
    }
}
