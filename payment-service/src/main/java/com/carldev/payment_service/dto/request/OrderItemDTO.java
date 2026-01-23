<<<<<<<< HEAD:payment-service/src/main/java/com/carldev/payment_service/kafka/eventsDTO/OrderItemDTO.java
package com.carldev.payment_service.kafka.eventsDTO;
========
package com.carldev.payment_service.dto.request;
>>>>>>>> developer:payment-service/src/main/java/com/carldev/payment_service/dto/request/OrderItemDTO.java

import java.math.BigDecimal;

public record OrderItemDTO(
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
