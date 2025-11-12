package com.carldev.auth_service.exception;

public class UserExistEmailException extends RuntimeException {

    public UserExistEmailException(String message){
        super(message);
    }
}
