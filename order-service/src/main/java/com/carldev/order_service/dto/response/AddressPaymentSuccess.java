package com.carldev.order_service.dto.response;

public record AddressPaymentSuccess(
        String streetLine1,
        String streetLine2,
        String city,
        String state,
        String country
) {

}
