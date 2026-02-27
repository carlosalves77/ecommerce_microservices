package com.carldev.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;


public record AddressRequestDTO(

        @NotBlank(message = "Informe o primeiro endereço")
        String streetLine1,
        String streetLine2,
        @NotBlank(message = "Informe o nome da cidade")
        String city,
        @NotBlank(message = "Informe o nome do estado")
        String state,
        @NotBlank(message = "Informe o nome do país")
        String country,
        boolean defaultAddress
) {
}
