package com.carldev.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPassword(
        @NotBlank(message = "Informe o email")
        String email
) {
}
