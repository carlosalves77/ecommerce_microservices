package com.carldev.auth_service.dto.response;

import java.util.UUID;

public record JwtUserData(
        UUID userId, String email
) {
}
