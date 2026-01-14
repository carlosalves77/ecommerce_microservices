package com.carldev.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserIsNotVerifyAccountException extends RuntimeException {
    public UserIsNotVerifyAccountException(String message) {
        super(message);
    }
}
