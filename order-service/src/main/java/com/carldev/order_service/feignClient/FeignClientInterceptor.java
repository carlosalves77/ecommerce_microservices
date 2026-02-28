package com.carldev.order_service.feignClient;

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
            return;
        }

        if (authentication.getPrincipal() instanceof Jwt) {

            Jwt jwt = (Jwt) authentication.getPrincipal();

            String tokenValue = jwt.getTokenValue();
            requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);
        }
    }
}
