package com.pm.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.jsonwebtoken.security.Keys;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${jwt.secret}") String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret.getBytes(StandardCharsets.UTF_8));
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    @Order(1)
    public SecurityWebFilterChain authPublicChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/auth/**"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain apiChain(ServerHttpSecurity http) {
        // Convert claim `role` -> `ROLE_*`
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.GET, "/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // patients
                        .pathMatchers(HttpMethod.GET, "/api/patients/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
                        .pathMatchers(HttpMethod.POST, "/api/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.PUT, "/api/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.DELETE, "/api/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")

                        // appointments
                        .pathMatchers(HttpMethod.GET, "/api/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
                        .pathMatchers(HttpMethod.POST, "/api/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.PUT, "/api/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN")

                        // doctors
                        .pathMatchers(HttpMethod.GET, "/api/doctors/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.POST, "/api/doctors/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.PUT, "/api/doctors/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .pathMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")

                        // everything else requires authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
                        ))
                )
                .build();
    }
}

//DEMO in order
//1. Token comes in header -> SecurityConfig picks place-> filter of chains are initialzed for all the endpoints
// 2. oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
//          new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
// )) this kicks up-> spring security is told that Bearer JwtToken will be there for request
//    ->spring sec retrieves the token,
//    ->calls ReactiveJwtDecoder which verifies the token's signature & expiry with secret key
//    ->token is converted into JWT claims object
//    ->jwt.JwtAuthenticationConverter starts to convert token to Authentication object
//    ->ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter) is called which extracts the
//            role from token and builds a SimpleGrantedAuthority(Role_%ROLE%) object and sets in spring sec context
//    ->endpoint is reached and verifies the Role of endpoint required with Role in context set
//    ->if matches, routes to endpoint, else 401/405
