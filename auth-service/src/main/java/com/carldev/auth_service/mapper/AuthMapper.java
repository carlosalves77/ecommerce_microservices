package com.carldev.auth_service.mapper;

import com.carldev.auth_service.dto.response.AuthResponseDTO;
import com.carldev.auth_service.model.UserAuth;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {


    AuthResponseDTO toDto(UserAuth userAuth);
}
