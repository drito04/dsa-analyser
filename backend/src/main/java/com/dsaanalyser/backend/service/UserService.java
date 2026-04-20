package com.dsaanalyser.backend.service;

import com.dsaanalyser.backend.dto.AuthRequest;
import com.dsaanalyser.backend.dto.AuthResponse;
import com.dsaanalyser.backend.dto.RegisterRequest;
import com.dsaanalyser.backend.model.User;
import com.dsaanalyser.backend.repository.UserRepository;
import com.dsaanalyser.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user account.
     * - Validates that username and email are not already taken.
     * - BCrypt-hashes the password before persisting.
     * - Returns a signed JWT so the user is immediately logged in after registration.
     *
     * @param request RegisterRequest DTO (username, email, password)
     * @return AuthResponse containing the signed JWT and username
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username '" + request.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with email '" + request.getEmail() + "' already exists.");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);
        log.info("New user registered: username='{}'", newUser.getUsername());

        String token = jwtUtil.generateToken(newUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .message("Registration successful. Welcome!")
                .build();
    }

    /**
     * Authenticates an existing user and returns a JWT.
     * - Delegates credential verification to Spring Security's AuthenticationManager.
     * - Throws BadCredentialsException (HTTP 401) on wrong username/password.
     *
     * @param request AuthRequest DTO (username, password)
     * @return AuthResponse containing the signed JWT and username
     */
    public AuthResponse login(AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow();

            log.info("User logged in: username='{}'", userDetails.getUsername());

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .message("Login successful.")
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username='{}'", request.getUsername());
            throw new BadCredentialsException("Invalid username or password.");
        }
    }
}