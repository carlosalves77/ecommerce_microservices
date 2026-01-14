package com.carldev.auth_service.dto.request;

public record ResetPasswordDTO(
        String token,
        String newPassword
) {
}
