package com.carldev.auth_service.dto.response;

import com.carldev.auth_service.util.RoleType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.time.LocalDateTime;

public record AuthResponseDTO(
        String email,
        String username,
        LocalDateTime created_at,
        Instant lastLoginAt,
        Boolean isVerified,
        @Enumerated(EnumType.STRING)
        RoleType role
) {
}
