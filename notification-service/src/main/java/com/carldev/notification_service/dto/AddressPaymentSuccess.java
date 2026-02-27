package com.carldev.notification_service.dto;

public record AddressPaymentSuccess(
        String streetLine1,
        String streetLine2,
        String city,
        String state,
        String country
) {

}
