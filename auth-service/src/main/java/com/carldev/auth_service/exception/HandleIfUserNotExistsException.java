package com.carldev.auth_service.exception;

public class HandleIfUserNotExistsException extends RuntimeException{

    public HandleIfUserNotExistsException(String message) {
        super(message);
    }
}
