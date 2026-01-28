package com.carldev.auth_service.service;

import com.carldev.auth_service.config.TokenConfig;
import com.carldev.auth_service.dto.request.AuthLoginRequestDTO;
import com.carldev.auth_service.dto.request.AuthRegisterRequestDTO;
import com.carldev.auth_service.dto.request.ForgotPassword;
import com.carldev.auth_service.dto.request.ResetPasswordDTO;
import com.carldev.auth_service.dto.response.AuthLoginResponseDTO;
import com.carldev.auth_service.dto.response.AuthRegisterResponseDTO;
import com.carldev.auth_service.dto.response.AuthResponseDTO;
import com.carldev.auth_service.exception.*;
import com.carldev.auth_service.kafka.eventsDTO.CreateAccountValidationEvent;
import com.carldev.auth_service.kafka.eventsDTO.ResetPasswordEvent;
import com.carldev.auth_service.mapper.AuthMapper;
import com.carldev.auth_service.mapper.AuthRegisterMapper;
import com.carldev.auth_service.model.ResetToken;
import com.carldev.auth_service.model.UserAuth;
import com.carldev.auth_service.model.VerificationToken;
import com.carldev.auth_service.repository.AuthRepository;
import com.carldev.auth_service.repository.ResetTokenRepository;
import com.carldev.auth_service.repository.TokenVerificationRepository;
import com.carldev.auth_service.util.SecureR;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAuthService {

    private final AuthRepository authRepository;
    private final TokenConfig tokenConfig;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthRegisterMapper authRegisterMapper;
    private final AuthMapper authMapper;
    private final TokenVerificationRepository tokenVerificationRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserAuthService(AuthRepository authRepository, TokenConfig tokenConfig,
                           AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                           AuthRegisterMapper authRegisterMapper, AuthMapper authMapper,
                           TokenVerificationRepository tokenVerificationRepository,
                           ResetTokenRepository resetTokenRepository,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.authRepository = authRepository;
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authRegisterMapper = authRegisterMapper;
        this.authMapper = authMapper;
        this.tokenVerificationRepository = tokenVerificationRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.applicationEventPublisher = applicationEventPublisher;
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

        String verificationLink = "http://localhost:4000/api/auth/verify-account" +
                "?token=" + verificationToken.getToken();


        CreateAccountValidationEvent accountValidationEvent = new CreateAccountValidationEvent
                        (verificationLink, saveUserAuth.getUsername(), saveUserAuth.getEmail());

        applicationEventPublisher.publishEvent(accountValidationEvent);

        return authRegisterMapper.toDto(saveUserAuth);
    }


    public AuthLoginResponseDTO loginUser(AuthLoginRequestDTO requestDTO) {

        try {
            UsernamePasswordAuthenticationToken userPass =
                    new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password());

            Authentication authentication = authenticationManager.authenticate(userPass);
            UserAuth userAuth = (UserAuth) authentication.getPrincipal();

            if (userAuth.getIsVerified().equals(false)) {
                throw new UserIsNotVerifyAccountException("Usuário não verificado");
            }

            userAuth.setLastLoginAt(Instant.now());
            authRepository.save(userAuth);
            String token = tokenConfig.generateToken(userAuth);

            return new AuthLoginResponseDTO(userAuth.getUsername(), token);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }

    }

    public void forgotPassword(ForgotPassword resetPasswordDTO) {
        Optional<UserAuth> findUserByEmail = authRepository.findByEmailIgnoreCase(resetPasswordDTO.email());

        if (findUserByEmail.isPresent()) {
            UserAuth userAuth = findUserByEmail.get();
            SecureR secureR = new SecureR();
            String token = secureR.makeResetToken();

            ResetToken resetToken = new ResetToken();
            resetToken.setToken(token);
            resetToken.setUserAuth(userAuth);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

            ResetPasswordEvent resetPasswordEvent = new ResetPasswordEvent(
                    resetToken.getToken(), userAuth.getUsername(), userAuth.getEmail()
            );

            applicationEventPublisher.publishEvent(resetPasswordEvent);

            resetTokenRepository.save(resetToken);

        }
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO passwordDTO) {
        Optional<ResetToken> tokenOptional = resetTokenRepository.findByToken(passwordDTO.token());

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            throw new HandleIfInvalidTokenOrExpireException("Token está vazio ou inválido");
        }

        ResetToken tokenRecord = tokenOptional.get();
        UserAuth userAuth = tokenRecord.getUserAuth();

        userAuth.setPassword(passwordEncoder.encode(passwordDTO.newPassword()));
        authRepository.save(userAuth);
        resetTokenRepository.delete(tokenRecord);
    }

    @Transactional
    public void verifyAccount(String token) {

        VerificationToken verificationToken =
                tokenVerificationRepository.findByToken(token).orElseThrow(() -> new RuntimeException(
                        "Token de verificação expirada"));

        if (verificationToken.isExpired()) {
            tokenVerificationRepository.delete(verificationToken);
            throw new RuntimeException("Token de verificação expirada");

        }

        UserAuth userAuth = verificationToken.getUserAuth();

        userAuth.setIsVerified(true);
        authRepository.save(userAuth);

        tokenVerificationRepository.delete(verificationToken);
    }

    @Transactional
    public List<AuthResponseDTO> getUserByName(String username) {

        Pageable pageable = PageRequest.of(0, 10);

        List<UserAuth> authList = authRepository.findByUsernameContainingIgnoreCase(username.trim(),
                pageable);

        return authList.stream().map(authMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthResponseDTO findUserById(UUID uuid) {

        UserAuth user = authRepository.findById(uuid).orElseThrow(
                () -> new HandleIfUserNotExistsException("Usuário não encontrado")
        );

        return authMapper.toDto(user);
    }

    public AuthResponseDTO disableOrEnableAccount(UUID uuid, boolean isVerify) {

        UserAuth user = authRepository.findById(uuid).orElseThrow(
                () -> new HandleIfUserNotExistsException("Usuário não encontrado")
        );

        if (isVerify == user.getIsVerified().equals(true)) {
            throw new UserIsAlreadyVerifiedException("Usuário já está verificado");
        }

        user.setIsVerified(isVerify);
        authRepository.save(user);

        return authMapper.toDto(user);
    }

    public Page<AuthResponseDTO> getAllUsers(int pageNumber) {

        int page = pageNumber - 1;

        Pageable pageable = PageRequest.of(page, 10);

        Page<UserAuth> users = authRepository.findAll(pageable);


        return users.map(authMapper::toDto);
    }

}
