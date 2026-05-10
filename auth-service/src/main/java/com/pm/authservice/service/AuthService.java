package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDto;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.exception.EmailAlreadyExistException;
import com.pm.authservice.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserSevice userSevice;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserSevice userSevice, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userSevice = userSevice;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public Optional<String> authenticate(LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        String token = jwtUtil.generateToken(authentication);
        if(token == null) {
            log.error("Token generation failed in jwt");
            return Optional.empty();
        }
        log.info("Generated token in service: {}", token);
        return Optional.of(token);
    }

    public boolean validate(String token) {
        try {
            jwtUtil.validateToken(token);
            log.info("Token is validated successfully: {}", token);
            return true;
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String signUp(UserDTO userDTO) {
        log.info("Sign-up attempt for email={}", userDTO.getEmail());
        if (userSevice.emailExist(userDTO.getEmail())) {
            throw new EmailAlreadyExistException("Email already Exists: " + userDTO.getEmail());
        }

        String role = userSevice.saveUser(userDTO);
        log.info("Sign-up successful for email={}, role={}", userDTO.getEmail(), role);
        return role;
    }
}
