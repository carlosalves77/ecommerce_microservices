package com.carldev.auth_service.dto.request;

import com.carldev.auth_service.util.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRegisterRequestDTO(

        @NotBlank(message = "Informe o e-mail")
        @Column(unique = true)
        String email,

        @NotBlank(message = "Informe a senha")
        String password,

        @NotBlank()
        String username,

        @NotNull(message = "Informe o tipo de usuário")
        @Enumerated(EnumType.STRING)
        RoleType role
) {


}
