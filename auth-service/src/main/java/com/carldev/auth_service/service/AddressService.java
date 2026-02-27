package com.carldev.auth_service.service;

import com.carldev.auth_service.dto.request.AddressRequestDTO;
import com.carldev.auth_service.dto.request.AddressUpdateRequestDTO;
import com.carldev.auth_service.dto.response.AddressResponseDTO;
import com.carldev.auth_service.exception.AddressNotExistsException;
import com.carldev.auth_service.exception.HandleIfUserNotExistsException;
import com.carldev.auth_service.mapper.AddressMapper;
import com.carldev.auth_service.model.Address;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.repository.AddressRepository;
import com.carldev.auth_service.repository.AuthRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressService {

    private final AddressRepository repository;
    private final AddressMapper addressMapper;
    private final AuthRepository authRepository;


    public AddressService(AddressRepository repository, AddressMapper addressMapper,
                          AuthRepository authRepository) {
        this.repository = repository;
        this.addressMapper = addressMapper;

        this.authRepository = authRepository;
    }


    @Transactional
    public AddressResponseDTO createAddress(UUID id, AddressRequestDTO addressRequestDTO) {

        UserAuth userAuth = authRepository.findById(id).orElseThrow(
                () -> new HandleIfUserNotExistsException("Usuário não encontrado")
        );

        boolean isFirstAddress = !repository.existsByUserAuthUserId(id);


        Address address = addressMapper.toEntity(addressRequestDTO);
        address.setUserAuth(userAuth);

        if (isFirstAddress) {
            address.setDefaultAddress(true);
        } else if (address.isDefaultAddress()) {
            repository.removeDefaultFromAllUserAddresses(id);
            address.setDefaultAddress(addressRequestDTO.defaultAddress());
        } else {
            address.setDefaultAddress(false);
        }

        Address savedAddress = repository.save(address);

        return addressMapper.toDto(savedAddress);
    }

    public List<AddressResponseDTO> getAddress(UUID id) {

        UserAuth userAuth = authRepository.findById(id).orElseThrow(
                () -> new AddressNotExistsException("Usuário não encontrado")
        );

        List<Address> getAddress = repository.findAllByUserAuthUserId(userAuth.getUserId());

        return getAddress.stream().map(addressMapper::toDto)
                .collect(Collectors.toList());

    }

    @Transactional
    public AddressResponseDTO updateAddress(UUID userId, UUID addressId,
                                            AddressUpdateRequestDTO updateRequestDTO)
            throws AccessDeniedException {

        Address address = repository.findById(addressId).orElseThrow(
                () -> new AddressNotExistsException("Endereço não encontrado")
        );

        if (!address.getUserAuth().getUserId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão");
        }

        Address entityAddress = addressMapper.toUpdateEntity(updateRequestDTO);
        address.setStreetLine1(entityAddress.getStreetLine1());
        address.setStreetLine2(entityAddress.getStreetLine2());
        address.setCity(entityAddress.getCity());
        address.setState(entityAddress.getState());
        address.setCountry(entityAddress.getCountry());

        Address saveAddress = repository.save(entityAddress);

        return addressMapper.toDto(saveAddress);
    }


    public AddressResponseDTO findAddressById(UUID id, UUID userId) throws AccessDeniedException {

        UserAuth userAuth = authRepository.findById(userId).orElseThrow(
                () -> new HandleIfUserNotExistsException("Usuário não encontrado")
        );

        Address address = repository.findById(id).orElseThrow(
                () -> new AddressNotExistsException("Endereço não encontrado")
        );

        if (!address.getUserAuth().getUserId().equals(userAuth.getUserId())) {
            throw new AccessDeniedException("Você não tem permissão");
        }

        return addressMapper.toDto(address);
    }

    public void deleteAddressById(UUID id) {

        Address address = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Id não encontrado")
        );

        repository.deleteById(address.getAddressId());

    }


}
