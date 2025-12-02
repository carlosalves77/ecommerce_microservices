package com.carldev.notification_service.dto;

import java.math.BigDecimal;

public record ItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {

}
