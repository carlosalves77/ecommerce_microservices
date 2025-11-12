package com.carldev.auth_service.service;

import com.carldev.auth_service.config.TokenConfig;
import com.carldev.auth_service.dto.authDTO.request.AuthLoginRequestDTO;
import com.carldev.auth_service.dto.authDTO.request.AuthRegisterRequestDTO;
import com.carldev.auth_service.dto.authDTO.response.AuthLoginResponseDTO;
import com.carldev.auth_service.dto.authDTO.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.exception.UserExistEmailException;
import com.carldev.auth_service.mapper.AuthRegisterMapper;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.model.VerificationToken;
import com.carldev.auth_service.repository.AuthRepository;
import com.carldev.auth_service.repository.TokenVerificationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class UserAuthService {

    private final AuthRepository authRepository;
    private final TokenConfig tokenConfig;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthRegisterMapper authRegisterMapper;
    private final TokenVerificationRepository tokenVerificationRepository;


    public UserAuthService(AuthRepository authRepository, TokenConfig tokenConfig,
                           AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                           AuthRegisterMapper authRegisterMapper, TokenVerificationRepository tokenVerificationRepository) {
        this.authRepository = authRepository;
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authRegisterMapper = authRegisterMapper;
        this.tokenVerificationRepository = tokenVerificationRepository;
    }

    public AuthRegisterResponseDTO registerUser(AuthRegisterRequestDTO authRegisterRequestDTO) {


        if (authRepository.existsByEmail(authRegisterRequestDTO.email())) {
            throw new UserExistEmailException("E-mail já existente: ");
        }

        UserAuth userAuth = authRegisterMapper.toEntity(authRegisterRequestDTO);

        String encodePassword = passwordEncoder.encode(authRegisterRequestDTO.password());
        userAuth.setPassword(encodePassword);
        userAuth.setIsVerified(false);
        UserAuth saveUserAuth = authRepository.save(userAuth);

        VerificationToken verificationToken = new VerificationToken(saveUserAuth);
        tokenVerificationRepository.save(verificationToken);

        String verificationLink =
                "http://localhost:4004/api/auth/verify-account?token="+ verificationToken.getToken();
        log.info("Token enviado para: {}", saveUserAuth.getEmail());
        log.info("Link de verificação: {}", verificationLink);

        return authRegisterMapper.toDto(saveUserAuth);
    }


    public AuthLoginResponseDTO loginUser(AuthLoginRequestDTO requestDTO) {

        UsernamePasswordAuthenticationToken userPass =
                new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password());

        Authentication authentication = authenticationManager.authenticate(userPass);
        UserAuth userAuth = (UserAuth) authentication.getPrincipal();
        userAuth.setLastLoginAt(Instant.now());
        authRepository.save(userAuth);
        String token = tokenConfig.generateToken(userAuth);

        return new AuthLoginResponseDTO(userAuth.getUsername(), token);
    }

    @Transactional
    public void verifyAccount(String token) {

        VerificationToken verificationToken = tokenVerificationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de verificação expirada"));


        if (verificationToken.isExpired()) {
            tokenVerificationRepository.delete(verificationToken);
            throw new RuntimeException("Token de verificação expirada");

        }

        UserAuth userAuth = verificationToken.getUserAuth();

        userAuth.setIsVerified(true);
        authRepository.save(userAuth);

        tokenVerificationRepository.delete(verificationToken);
    }


}
