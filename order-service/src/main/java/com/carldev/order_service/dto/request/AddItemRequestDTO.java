package com.carldev.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AddItemRequestDTO(
        @NotBlank(message = "Informe o nome do sku")
        String sku,
        @NotEmpty(message = "Informe a quantidade")
        int quantity
) {
}
