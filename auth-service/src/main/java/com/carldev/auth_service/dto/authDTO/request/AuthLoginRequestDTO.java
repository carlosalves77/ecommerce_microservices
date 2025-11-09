package com.carldev.auth_service.dto.authDTO.request;

public record AuthLoginRequestDTO (
        String email,
        String password
) {
}
