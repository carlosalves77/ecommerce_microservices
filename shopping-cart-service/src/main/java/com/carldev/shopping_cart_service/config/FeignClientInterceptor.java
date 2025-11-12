package com.carldev.shopping_cart_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Slf4j
@Configuration
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null) {

            log.warn("Interceptor Feign: 'Authentication' está NULO. Nenhuma header será adicionada.");
            return;
        }

        if (authentication.getPrincipal() instanceof Jwt) {

            Jwt jwt = (Jwt) authentication.getPrincipal();

            String tokenValue = jwt.getTokenValue();

            log.info("Interceptor Feign: Adicionando Bearer Token na requisição para -> {}"
                    , requestTemplate.url());

            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        } else {
            log.warn("Interceptor Feign: 'Principal' não é uma instância de Jwt. Tipo: {}"
                    , authentication.getPrincipal().getClass().getName());
        }
    }
}
