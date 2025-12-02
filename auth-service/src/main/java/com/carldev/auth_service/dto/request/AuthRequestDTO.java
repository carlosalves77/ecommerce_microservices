package com.carldev.auth_service.dto.request;

public record AuthRequestDTO(
        String username,
        Boolean isVerified
) {
}
