package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDto;
import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.exception.EmailAlreadyExistException;
import com.pm.authservice.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserSevice userSevice;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserSevice userSevice, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userSevice = userSevice;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDto loginRequestDto) {

        Optional<String> token = userSevice.findByEmail(loginRequestDto.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getEmail(), user.getRole()));
        log.info("Generated token in service: {}", token.orElse("No token generated"));
        return token;
    }

    public boolean validate(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String signUp(UserDTO userDTO) {
        if(userSevice.emailExist(userDTO.getEmail())){
            throw new EmailAlreadyExistException("Email already Exists: "+userDTO.getEmail());
        }

        return userSevice.saveUser(userDTO);
    }
}
