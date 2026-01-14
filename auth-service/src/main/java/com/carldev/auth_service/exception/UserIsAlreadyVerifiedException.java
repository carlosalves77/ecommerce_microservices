package com.carldev.auth_service.exception;

public class UserIsAlreadyVerifiedException extends RuntimeException {
    public UserIsAlreadyVerifiedException(String message) {
        super(message);
    }
}
