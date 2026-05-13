package com.pm.authservice.config;

import com.pm.authservice.entity.User;
import com.pm.authservice.service.UserSevice;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserSevice userSevice;

    public CustomUserDetailService(UserSevice userSevice) {
        this.userSevice = userSevice;
    }

    // here username can by anything, like its email for our use
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userSevice.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getEnabled(),
                true, true, true,
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_"+user.getRole().name())
                )
        );
    }
}
