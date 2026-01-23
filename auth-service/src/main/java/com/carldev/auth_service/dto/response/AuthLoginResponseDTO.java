package com.carldev.auth_service.dto.response;

public record AuthLoginResponseDTO(
        String username,
        String token
) {
}
