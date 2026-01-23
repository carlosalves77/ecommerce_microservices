package com.carldev.auth_service.service;

import com.carldev.auth_service.config.TokenConfig;
import com.carldev.auth_service.dto.request.AuthLoginRequestDTO;
import com.carldev.auth_service.dto.request.AuthRegisterRequestDTO;
import com.carldev.auth_service.dto.response.AuthLoginResponseDTO;
import com.carldev.auth_service.dto.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.kafka.eventsDTO.CreateAccountValidationEvent;
import com.carldev.auth_service.mapper.AuthRegisterMapper;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.model.VerificationToken;
import com.carldev.auth_service.repository.AuthRepository;
import com.carldev.auth_service.repository.TokenVerificationRepository;
import com.carldev.auth_service.util.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @InjectMocks
    private UserAuthService userAuthService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private TokenVerificationRepository tokenVerificationRepository;

    @Mock
    private AuthRegisterMapper authRegisterMapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Mock
    private TokenConfig tokenConfig;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;


    @Test
    @DisplayName("Deve registar um usuário com sucesso quando e-mail não existir")
    void shouldRegisterUserSuccessfully() {

        String email = "carlos@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";


        AuthRegisterRequestDTO registerRequestDTO = new AuthRegisterRequestDTO(
                email,
                rawPassword,
                "Carlos",
                RoleType.ADMIN
        );


        UserAuth userAuthEntity = new UserAuth();
        userAuthEntity.setEmail("carlo@example.com");

        UUID id = UUID.randomUUID();

        UserAuth saveUserAuth = new UserAuth();
        saveUserAuth.setUserId(id);
        saveUserAuth.setEmail("carlo@example.com");
        saveUserAuth.setPassword(encodedPassword);

        LocalDateTime createAt = LocalDateTime.now();

        AuthRegisterResponseDTO responseDTO = new AuthRegisterResponseDTO(
                "carlos@example.com",
                "Carlos",
                createAt,
                RoleType.ADMIN
        );


        when(authRepository.existsByEmail(registerRequestDTO.email())).thenReturn(false);
        when(authRegisterMapper.toEntity(registerRequestDTO)).thenReturn(userAuthEntity);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(authRepository.save(any(UserAuth.class))).thenReturn(saveUserAuth);
        when(authRegisterMapper.toDto(any(UserAuth.class))).thenReturn(responseDTO);


        AuthRegisterResponseDTO result = userAuthService.registerUser(registerRequestDTO);

        assertNotNull(result);
        assertEquals(responseDTO.email(), result.email());

        verify(passwordEncoder, times(1)).encode(rawPassword);

        verify(authRepository, times(1)).save(any(UserAuth.class));

        verify(tokenVerificationRepository, times(1)).save(any(VerificationToken.class));
    }

    @DisplayName("Deve retornar sucesso quando efetuar o login corretamente")
    @Disabled("TODO/ Consertar Test")
    @Test
    void ShouldReturnLoginSuccessfully() {

        AuthLoginRequestDTO loginRequestDTO = new AuthLoginRequestDTO(
                "carlos@example.com",
                "password123"
        );

        UserAuth saveUserAuth = new UserAuth();
        saveUserAuth.setUsername("Carlos");
        saveUserAuth.setPassword("password123");
//
//        String verificationLink = "http://localhost:4000/api/auth/verify-account" +
//                "?token=" + verificationToken.getToken();

        CreateAccountValidationEvent accountValidationEvent = new CreateAccountValidationEvent(
                "12345",
                "Carlos",
                "carlos@example.com"
        );

        Authentication authMock = mock(Authentication.class);



        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(saveUserAuth);
        when(tokenConfig.generateToken(saveUserAuth)).thenReturn("token.jwt.falso");
        when(CreateAccountValidationEvent.fromEntity(
                accountValidationEvent.accountValidation(),
                accountValidationEvent.userName(),
                accountValidationEvent.email()
        )).thenReturn(accountValidationEvent);


        AuthLoginResponseDTO authLoginResponseDTO = userAuthService.loginUser(loginRequestDTO);

        assertNotNull(authLoginResponseDTO);
        assertEquals("token.jwt.falso", authLoginResponseDTO.token());
        assertEquals("Carlos", authLoginResponseDTO.username());

        verify(authRepository, times(1)).save(saveUserAuth);
        verify(applicationEventPublisher, times(1)).publishEvent(any(ApplicationEventPublisher.class));
        assertNotNull(saveUserAuth.getLastLoginAt());

    }

}