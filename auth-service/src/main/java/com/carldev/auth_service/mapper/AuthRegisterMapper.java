package com.carldev.auth_service.mapper;

import com.carldev.auth_service.dto.authDTO.request.AuthRegisterRequestDTO;
import com.carldev.auth_service.dto.authDTO.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.model.UserAuth;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthRegisterMapper {

    UserAuth toEntity(AuthRegisterRequestDTO authRegisterRequestDTO);

    AuthRegisterResponseDTO toDto(UserAuth userAuth);
}
