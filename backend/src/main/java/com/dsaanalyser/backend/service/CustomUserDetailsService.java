package com.dsaanalyser.backend.service;

import com.dsaanalyser.backend.model.User;
import com.dsaanalyser.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security hook — loads a User from the DB by username
 * so the AuthenticationManager can verify credentials during login,
 * and so the JWT filter can validate tokens on every protected request.
 *
 * Wired into SecurityConfig via:
 *   http.userDetailsService(customUserDetailsService)
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username: " + username));

        // Returns a Spring Security User object with username, hashed password, and roles.
        // Empty authorities list = no role-based access control (all authenticated users are equal).
        // Expand this with GrantedAuthority if you add ADMIN / EDUCATOR roles later.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}