package com.carldev.auth_service.repository;

import com.carldev.auth_service.model.UserAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<UserAuth, UUID> {

    Optional<UserDetails> findUserByEmail(String username);

    boolean existsByEmail(String email);

    Optional<UserAuth> findByEmailIgnoreCase(String email);

    List<UserAuth> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

}
