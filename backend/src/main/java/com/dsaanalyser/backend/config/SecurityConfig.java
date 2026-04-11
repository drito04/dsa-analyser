package com.dsaanalyser.backend.config;

import com.dsaanalyser.backend.security.JwtAuthFilter;
import com.dsaanalyser.backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration for the DSA Analyzer.
 *
 * Key decisions:
 *  - Stateless sessions (STATELESS) — the server holds no session state.
 *    All authentication is done via the JWT on each request.
 *  - CSRF disabled — not needed for stateless REST APIs where
 *    the client sends an Authorization header (not cookies).
 *  - Public routes: /api/auth/** and GET /api/problems/**
 *  - Protected routes: /api/analyze and /api/submissions/me require a valid JWT.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Main security filter chain.
     * Defines which routes are public vs protected and where the JWT filter sits.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT-based REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // Disable default form login and HTTP Basic — we use JWT only
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Route-level authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Auth endpoints — always public
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // Problem browsing — public so unauthenticated users can explore problems
                        // POST/PUT/DELETE on problems would be admin-only (add later if needed)
                        .requestMatchers(HttpMethod.GET, "/api/problems", "/api/problems/**").permitAll()

                        // All other endpoints require a valid JWT
                        .anyRequest().authenticated()
                )

                // Stateless session — Spring Security will never create an HttpSession
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Wire in the custom AuthenticationProvider (uses our UserDetailsService + BCrypt)
                .authenticationProvider(authenticationProvider())

                // Add our JWT filter BEFORE the standard username/password filter
                // so the JWT is validated and the SecurityContext is populated first
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationProvider — tells Spring Security how to load and verify users.
     * Wires together:
     *  - CustomUserDetailsService  → loads User from MySQL by username
     *  - BCryptPasswordEncoder     → verifies the hashed password
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager — used directly by UserService.login()
     * to authenticate credentials programmatically.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * BCryptPasswordEncoder — used by UserService.register() to hash passwords
     * before saving, and by the AuthenticationProvider to verify them on login.
     *
     * Strength 12 is the recommended balance between security and performance.
     * Higher values increase hashing time (protects against brute force)
     * but add latency to every login request.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}