package com.pm.apigateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Component
public class CustomExceptionHandler implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);
    ObjectMapper objectMapper = new ObjectMapper();

    /*
     * Handles 401 Unauthorized
     * Triggered when:
     * - JWT missing
     * - JWT invalid
     * - JWT expired
     * - User unauthenticated
     */
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String message;

        // Try to extract message from exception
        if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
            message = ex.getMessage();
        } else {
            message = "Authentication required";
        }

        log.warn(
                "401 Unauthorized -> path={}, method={}, reason={}",
                exchange.getRequest().getPath(),
                exchange.getRequest().getMethod(),
                message
        );

        return errorResponseBuilder(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", message);
    }

    /**
     * Handles 403 Forbidden
     * Triggered when:
     * - JWT valid
     * - User authenticated
     * - Role insufficient
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {

                    String role = auth.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority()
                            .replace("ROLE_", "");

                    String message =
                            "Role- " + role + " is not allowed to access this endpoint";

                    return errorResponseBuilder(
                            exchange,
                            HttpStatus.FORBIDDEN,
                            "Forbidden",
                            message
                    );
                });
    }

    private Mono<Void> errorResponseBuilder(ServerWebExchange exchange,
                                            HttpStatus status,
                                            String error,
                                            String message) {

        ServerHttpResponse response = exchange.getResponse();

        // Check if response is already committed
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot write error response");
            return Mono.empty();
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", error,
                "message", message
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);

            DataBuffer buffer =
                    response.bufferFactory().wrap(bytes);

            return response.writeWith(Flux.just(buffer))
                    .doOnError(e -> log.error("Error writing response body: status={}, error={}", status, error, e));

        } catch (Exception e) {
            log.error("Error while writing exception: ", e);
            return Mono.error(e);
        }
    }
}
