package com.carldev.auth_service.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.carldev.auth_service.model.UserAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenConfig {

    private final Algorithm algorithm;


    public TokenConfig(@Value("${JWT_SECRET}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Segredo JWT (JWT_SECRET) não pode ser nulo");
        }
        this.algorithm = Algorithm.HMAC256(apiKey);
    }

    public String generateToken(UserAuth userAuth) {
        return JWT.create().withClaim("userId", userAuth.getUserId().toString())
                .withSubject(userAuth.getEmail())
                .withExpiresAt(Instant.now()
                        .plusSeconds(86400)
                ).withIssuedAt(Instant.now())
                .sign(this.algorithm);
    }


    public Optional<JwtUserData> validateToken(String token) {
        try {
            DecodedJWT decode = JWT.require(this.algorithm).build().verify(token);
            return Optional.of(new JwtUserData(
                    UUID.fromString(decode.getClaim("userId").asString()),
                    decode.getSubject()
            ));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }


}
