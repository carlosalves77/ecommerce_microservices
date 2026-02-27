package com.carldev.auth_service.mapper;

import com.carldev.auth_service.dto.request.AddressRequestDTO;
import com.carldev.auth_service.dto.request.AddressUpdateRequestDTO;
import com.carldev.auth_service.dto.response.AddressResponseDTO;
import com.carldev.auth_service.model.Address;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDTO toDto(Address address);

    Address toEntity(AddressRequestDTO addressRequestDTO);

    Address toUpdateEntity(AddressUpdateRequestDTO addressUpdateRequestDTO);

}
