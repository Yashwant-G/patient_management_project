package com.pm.authservice.controller;

import com.pm.authservice.dto.LoginRequestDto;
import com.pm.authservice.dto.LoginResponseDto;
import com.pm.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate JWT token for authenticated user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDto);

        log.info("Recieved token in controller: {}", tokenOptional.orElse("No token generated"));
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenOptional.get();

        return ResponseEntity.ok().body(new LoginResponseDto(token));
    }

    @Operation(summary = "Validate JWT token")
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //Example token: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        String token = authHeader.substring(7);

        return authService.validate(token) ?
                ResponseEntity.ok().body("Token is valid") :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
    }

    //add signup
}
