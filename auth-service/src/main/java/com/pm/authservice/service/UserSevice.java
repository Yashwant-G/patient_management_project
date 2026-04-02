package com.pm.authservice.service;

import com.pm.authservice.entity.User;
import com.pm.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSevice {

    private final UserRepository userRepository;
    public UserSevice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
