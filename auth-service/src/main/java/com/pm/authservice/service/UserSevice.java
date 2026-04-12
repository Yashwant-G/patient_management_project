package com.pm.authservice.service;

import com.pm.authservice.dto.UserDTO;
import com.pm.authservice.entity.User;
import com.pm.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSevice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSevice(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Boolean emailExist(String email){
        return userRepository.existsByEmail(email);
    }

    public String saveUser(UserDTO userDTO){
        User user=new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(String.valueOf(userDTO.getRole()));

        return userRepository.save(user).getRole();
    }
}
