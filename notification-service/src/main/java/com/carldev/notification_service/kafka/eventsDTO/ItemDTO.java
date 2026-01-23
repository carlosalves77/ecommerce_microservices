package com.carldev.notification_service.kafka.eventsDTO;

import java.math.BigDecimal;

public record ItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {

}
