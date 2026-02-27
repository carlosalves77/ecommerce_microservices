package com.carldev.shopping_cart_service.dto.response;


import java.util.UUID;

public record AddressResponseDTO(
        UUID addressId,
        String streetLine1,
        String streetLine2,
        String city,
        String state,
        String country
) {

}
