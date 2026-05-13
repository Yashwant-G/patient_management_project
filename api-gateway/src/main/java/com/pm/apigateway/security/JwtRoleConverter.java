package com.pm.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Converts JWT claim "role" (e.g. "ADMIN") to Spring Security authority "ROLE_ADMIN".
 */
public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        if (role == null || role.isBlank()) {
            return List.of();
        }
        String normalizedRole = role.trim();
        if (normalizedRole.startsWith("ROLE_")) {
            return List.of(new SimpleGrantedAuthority(normalizedRole.toUpperCase(Locale.ROOT)));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole.toUpperCase(Locale.ROOT)));
    }
}
