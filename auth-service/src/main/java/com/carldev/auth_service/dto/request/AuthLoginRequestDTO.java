package com.carldev.auth_service.dto.request;

public record AuthLoginRequestDTO (
        String email,
        String password
) {
}
