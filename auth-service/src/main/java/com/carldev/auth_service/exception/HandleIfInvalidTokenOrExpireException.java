package com.carldev.auth_service.exception;

public class HandleIfInvalidTokenOrExpireException extends RuntimeException {
    public HandleIfInvalidTokenOrExpireException(String message) {
        super(message);
    }
}
