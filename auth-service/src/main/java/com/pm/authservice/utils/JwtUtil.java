package com.pm.authservice.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("PATIENT");

        log.info("Generating token for email: {} with role: {}", email, role);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hour expiration
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(token);
            log.info("Token is validated successfully: {}", token);
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature");
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token");
        }
    }
}
