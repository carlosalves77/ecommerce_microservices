package com.carldev.auth_service.service;

import com.carldev.auth_service.dto.request.AddressRequestDTO;
import com.carldev.auth_service.dto.response.AddressResponseDTO;
import com.carldev.auth_service.exception.HandleIfUserNotExistsException;
import com.carldev.auth_service.mapper.AddressMapper;
import com.carldev.auth_service.model.Address;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.repository.AddressRepository;
import com.carldev.auth_service.repository.AuthRepository;
import com.carldev.auth_service.util.RoleType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private AuthRepository authRepository;


    @Test
    @DisplayName("Deve criar o primeiro endereço como padrão")
    void shouldCreateAddressAsDefault() {

        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userId);
        userAuth.setRole(RoleType.USER);

        AddressRequestDTO requestDTO = new AddressRequestDTO(
                "Rua A, 123", "Apto 1", "São Paulo", "SP", "Brasil", false
        );

        Address address = new Address();
        address.setStreetLine1("Rua A, 123");
        address.setStreetLine2("Apto 1");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setCountry("Brasil");

        Address savedAddress = new Address();
        savedAddress.setAddressId(addressId);
        savedAddress.setStreetLine1("Rua A, 123");
        savedAddress.setDefaultAddress(true);
        savedAddress.setUserAuth(userAuth);

        AddressResponseDTO responseDTO = new AddressResponseDTO(
                addressId, "Rua A, 123", "Apto 1", "São Paulo", "SP", "Brasil", true
        );

        when(authRepository.findById(userId)).thenReturn(Optional.of(userAuth));
        when(addressRepository.existsByUserAuthUserId(userId)).thenReturn(false);
        when(addressMapper.toEntity(requestDTO)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(addressMapper.toDto(savedAddress)).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.createAddress(userId, requestDTO);

        assertNotNull(result);
        assertTrue(result.defaultAddress());
        assertTrue(address.isDefaultAddress());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Deve criar segundo endereço como não-padrão")
    void shouldCreateAddressNotDefault() {

        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();

        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userId);
        userAuth.setRole(RoleType.USER);

        AddressRequestDTO requestDTO = new AddressRequestDTO(
                "Rua B, 456", null, "Rio de Janeiro", "RJ", "Brasil", false
        );

        Address address = new Address();
        address.setStreetLine1("Rua B, 456");

        Address savedAddress = new Address();
        savedAddress.setAddressId(addressId);
        savedAddress.setDefaultAddress(false);

        AddressResponseDTO responseDTO = new AddressResponseDTO(
                addressId, "Rua B, 456", null, "Rio de Janeiro", "RJ", "Brasil", false
        );

        when(authRepository.findById(userId)).thenReturn(Optional.of(userAuth));
        when(addressRepository.existsByUserAuthUserId(userId)).thenReturn(true);
        when(addressMapper.toEntity(requestDTO)).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(addressMapper.toDto(savedAddress)).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.createAddress(userId, requestDTO);

        assertNotNull(result);
        assertFalse(result.defaultAddress());
        verify(addressRepository, never()).removeDefaultFromAllUserAddresses(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao criar endereço")
    void shouldThrowWhenUserNotFoundOnCreate() {

        UUID userId = UUID.randomUUID();
        AddressRequestDTO requestDTO = new AddressRequestDTO(
                "Rua A", null, "SP", "SP", "BR", false
        );

        when(authRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(HandleIfUserNotExistsException.class,
                () -> addressService.createAddress(userId, requestDTO));

        verify(addressRepository, never()).save(any());
    }


    @Test
    @DisplayName("Deve retornar lista de endereços do usuário")
    void shouldGetAddressesByUserId() {

        UUID userId = UUID.randomUUID();

        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userId);
        userAuth.setRole(RoleType.USER);

        Address address = new Address();
        address.setAddressId(UUID.randomUUID());
        address.setStreetLine1("Rua A");

        AddressResponseDTO dto = new AddressResponseDTO(
                address.getAddressId(), "Rua A", null, "SP", "SP", "BR", true
        );

        when(authRepository.findById(userId)).thenReturn(Optional.of(userAuth));
        when(addressRepository.findAllByUserAuthUserId(userId)).thenReturn(List.of(address));
        when(addressMapper.toDto(address)).thenReturn(dto);

        List<AddressResponseDTO> result = addressService.getAddress(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("Deve deletar endereço pelo ID")
    void shouldDeleteAddressById() {

        UUID addressId = UUID.randomUUID();

        Address address = new Address();
        address.setAddressId(addressId);

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        addressService.deleteAddressById(addressId);

        verify(addressRepository, times(1)).deleteById(addressId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando endereço não encontrado ao deletar")
    void shouldThrowWhenAddressNotFoundOnDelete() {

        UUID addressId = UUID.randomUUID();

        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> addressService.deleteAddressById(addressId));

        verify(addressRepository, never()).deleteById(any());
    }
}
