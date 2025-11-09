package com.carldev.auth_service.service;

import com.carldev.auth_service.repository.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthDetailService implements UserDetailsService {

    private final AuthRepository authRepository;

    public AuthDetailService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findUserByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
    }


}
