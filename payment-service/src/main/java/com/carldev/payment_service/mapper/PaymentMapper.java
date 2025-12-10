package com.carldev.payment_service.mapper;


import com.carldev.payment_service.dto.response.GetPaymentsResponseDTO;
import com.carldev.payment_service.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    GetPaymentsResponseDTO toDto(Payment payment);


}
