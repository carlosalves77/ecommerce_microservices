package com.carldev.auth_service.exception;

public class AddressNotExistsException extends RuntimeException {
    public AddressNotExistsException(String message) {
        super(message);
    }
}
