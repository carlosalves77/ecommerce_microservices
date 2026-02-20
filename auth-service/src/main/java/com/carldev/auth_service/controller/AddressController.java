package com.carldev.auth_service.controller;

import com.carldev.auth_service.dto.request.AddressRequestDTO;
import com.carldev.auth_service.dto.request.AddressUpdateRequestDTO;
import com.carldev.auth_service.dto.response.AddressResponseDTO;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @RequestBody AddressRequestDTO dto,
            Authentication authentication
    ) {

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        UUID userId = userAuth.getUserId();

        AddressResponseDTO saveAddress = addressService.createAddress(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(saveAddress);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAddress(
            Authentication authentication
    ) {

        UserAuth jwt = (UserAuth) authentication.getPrincipal();

        UUID userId = jwt.getUserId();

        List<AddressResponseDTO> getAddress = addressService.getAddress(userId);

        return ResponseEntity.status(HttpStatus.OK).body(getAddress);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable("id") UUID addressId,
            @RequestBody AddressUpdateRequestDTO dto,
            Authentication authentication
    ) throws AccessDeniedException {

        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        UUID userId = userAuth.getUserId();

        AddressResponseDTO responseDTO = addressService.updateAddress(userId, addressId, dto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable UUID id
    ) {
        addressService.deleteAddressById(id);

        return ResponseEntity.status(HttpStatus.OK).body("Endereço deletado");
    }

}
