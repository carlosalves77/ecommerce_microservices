package com.carldev.order_service.kafka.event;


import java.util.UUID;

public record AddressEvent(
        UUID addressId,
        String streetLine1,
        String streetLine2,
        String city,
        String state,
        String country
) {

}
