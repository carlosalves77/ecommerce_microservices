package com.carldev.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    public static final List<String> openApiEndPoints = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/verify-account",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/reset-password/confirm"
    );

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(JwtValidationGatewayFilterFactory.class);

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuild,
                                             @Value("${auth.service.url}") String authServiceUrl
    ) {
        this.webClient = webClientBuild.baseUrl(authServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (openApiEndPoints.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                    .uri("/api/v1/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange))
                    .onErrorResume(e -> {
                        if (e instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) e;
                            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                                logger.error("Endpoint não encontrado");
                                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return exchange.getResponse().setComplete();
                            }

                        }
                        logger.error("Erro na validação do token: {}", e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };

    }
}
