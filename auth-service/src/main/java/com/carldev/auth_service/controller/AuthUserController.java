package com.carldev.auth_service.controller;

import com.carldev.auth_service.config.SecurityFilter;
import com.carldev.auth_service.dto.request.*;
import com.carldev.auth_service.dto.response.AuthLoginResponseDTO;
import com.carldev.auth_service.dto.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.config.JwtTokenVerifier;
import com.carldev.auth_service.dto.response.AuthResponseDTO;
import com.carldev.auth_service.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
         try {
             authService.verifyAccount(token);
             return ResponseEntity.ok("Conta verificada com sucesso! Pode efetuar o login");
         } catch (RuntimeException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> forgotPassword(
            @Valid
            @RequestBody ForgotPassword dto
    ) {
        authService.forgotPassword(dto);

        return ResponseEntity.ok("Se o email estiver registrado, será enviado um link");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO dto) {
        authService.resetPassword(dto);

        return ResponseEntity.ok().body("Senha atualizada");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public ResponseEntity<List<AuthResponseDTO>> getUserByName(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "isVerified", defaultValue = "true") Boolean isVerified
            ) {

        AuthRequestDTO AuthRequest = new AuthRequestDTO(
                name,
                isVerified
        );

        List<AuthResponseDTO> responseDTO = authService.getUserByName(AuthRequest);

        return ResponseEntity.ok().body(responseDTO);
    }

}
