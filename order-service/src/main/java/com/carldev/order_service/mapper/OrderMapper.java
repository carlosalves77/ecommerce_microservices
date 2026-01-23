package com.carldev.order_service.mapper;

import com.carldev.order_service.dto.response.DeletePaymentResponseDTO;
import com.carldev.order_service.dto.response.OrderStatusResponseDTO;
import com.carldev.order_service.dto.response.OrdersResponseDTO;
import com.carldev.order_service.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrdersResponseDTO toDto(Order order);

    DeletePaymentResponseDTO deleteDto(Order order);

    OrderStatusResponseDTO toOrderStatusResponseDto(Order order);

}
