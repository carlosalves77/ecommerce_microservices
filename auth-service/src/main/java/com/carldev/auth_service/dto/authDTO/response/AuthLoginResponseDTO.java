package com.carldev.auth_service.dto.authDTO.response;

public record AuthLoginResponseDTO(
        String username,
        String token
) {
}
