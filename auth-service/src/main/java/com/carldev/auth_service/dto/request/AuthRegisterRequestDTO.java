package com.carldev.auth_service.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequestDTO(

        @NotBlank(message = "Informe o e-mail")
        @Column(unique = true)
        String email,

        @NotBlank(message = "Informe a senha")
        String password,

        @NotBlank(message = "Informe nome do usuário")
        String username

) {


}
