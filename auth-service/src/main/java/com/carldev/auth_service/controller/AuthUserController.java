package com.carldev.auth_service.controller;

import com.carldev.auth_service.dto.authDTO.request.AuthLoginRequestDTO;
import com.carldev.auth_service.dto.authDTO.request.AuthRegisterRequestDTO;
import com.carldev.auth_service.dto.authDTO.response.AuthLoginResponseDTO;
import com.carldev.auth_service.dto.authDTO.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.service.JwtTokenVerifier;
import com.carldev.auth_service.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthUserController {

    private final UserAuthService authService;
    private final JwtTokenVerifier tokenVerifier;

    public AuthUserController(UserAuthService authService, JwtTokenVerifier tokenVerifier) {
        this.authService = authService;
        this.tokenVerifier = tokenVerifier;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponseDTO> createUser(
            @Valid
            @RequestBody
            AuthRegisterRequestDTO requestDTO) {
        AuthRegisterResponseDTO createUser = authService.registerUser(requestDTO);

        return ResponseEntity.ok().body(createUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDTO> userLogin(
            @Valid
            @RequestBody
            AuthLoginRequestDTO requestDTO) {

        AuthLoginResponseDTO loginResponseDTO = authService.loginUser(requestDTO);

        return ResponseEntity.ok().body(loginResponseDTO);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> isValidToken(
            @RequestHeader("Authorization") String validToken) {

        return tokenVerifier.isTokenValid(validToken) ?
                ResponseEntity.ok().body("Token Valid") :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Invalid");
    }
}
