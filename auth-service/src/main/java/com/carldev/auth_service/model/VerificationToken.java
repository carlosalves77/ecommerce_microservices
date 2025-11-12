package com.carldev.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    private static final int EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserAuth userAuth;

    @Column(nullable = false)
    private Instant expiryDate;

    public VerificationToken(UserAuth userAuth) {
        this.userAuth = userAuth;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = Instant.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

}
