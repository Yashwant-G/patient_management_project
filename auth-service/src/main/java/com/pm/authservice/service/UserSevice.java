package com.pm.authservice.service;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.entity.User;
import com.pm.authservice.enums.RolesEnum;
import com.pm.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSevice {

    private static final Logger log = LoggerFactory.getLogger(UserSevice.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSevice(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public Optional<User> findByEmail(String email){
        log.info("Looking up user by email={}", email);
        return userRepository.findByEmail(email);
    }

    public Boolean emailExist(String email){
        return userRepository.existsByEmail(email);
    }

    public String saveUser(UserDTO userDTO){
        log.info("Saving new user with email={}, role={}", userDTO.getEmail(), userDTO.getRole());
        User user=new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(RolesEnum.valueOf(String.valueOf(userDTO.getRole())));

        String role = userRepository.save(user).getRole().name();
        log.info("User saved successfully with role={}", role);
        return role;
    }
}
