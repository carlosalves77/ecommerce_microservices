package com.carldev.auth_service.config;

import java.util.UUID;

public record JwtUserData(
        UUID userId, String email
) {
}
