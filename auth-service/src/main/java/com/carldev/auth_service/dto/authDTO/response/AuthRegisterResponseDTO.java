package com.carldev.auth_service.dto.authDTO.response;

import com.carldev.auth_service.util.RoleType;

import java.time.LocalDateTime;

public record AuthRegisterResponseDTO(
        String email,
        String username,
        LocalDateTime created_at,
        RoleType role
) {
}
