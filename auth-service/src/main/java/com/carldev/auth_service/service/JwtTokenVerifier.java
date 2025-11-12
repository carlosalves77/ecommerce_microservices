package com.carldev.auth_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenVerifier {


    @Value("${JWT_SECRET}")
    private String secret;

    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secret);

            this.jwtVerifier = JWT.require(algorithm).build();

        } catch (IllegalArgumentException e) {
            log.error("Error ao inicilizar o JWTVerifier {}", e.getMessage());
            throw new RuntimeException("Segredo JWT inválido");
        }
    }

    public boolean isTokenValid(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }
        try {

            String cleanToken = token.replace("Bearer ", "");
            this.jwtVerifier.verify(cleanToken);
            return true;
        } catch (JWTVerificationException e) {
            log.warn("Validação do JWT falhou: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error insesperado a validação do token", e);
            return false;
        }
    }


}
