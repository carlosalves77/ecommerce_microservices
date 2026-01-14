package com.carldev.auth_service.config;

import com.carldev.auth_service.dto.response.JwtUserData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver resolver;

    public SecurityFilter(TokenConfig tokenConfig, UserDetailsService userDetailsService,
                          @Qualifier("handlerExceptionResolver")
                          HandlerExceptionResolver resolver) {
        this.tokenConfig = tokenConfig;
        this.userDetailsService = userDetailsService;

        this.resolver = resolver;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String path = request.getRequestURI();
            if (path.startsWith("/api/auth/login") || path.equals("/api/auth/register")) {
              filterChain.doFilter(request, response);
                return;
            }

        String authorizedHeader = request.getHeader("Authorization");
        if (authorizedHeader != null && authorizedHeader.startsWith("Bearer ")) {
            String token = authorizedHeader.substring("Bearer ".length());

            Optional<JwtUserData> opUser = tokenConfig.validateToken(token);

            if (opUser.isPresent()) {
                JwtUserData userData = opUser.get();
                String email = userData.email();

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());

                System.out.println(authenticationToken);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);

        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }

    }

}
