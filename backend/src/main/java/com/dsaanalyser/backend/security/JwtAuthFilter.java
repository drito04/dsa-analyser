package com.dsaanalyser.backend.security;

import com.dsaanalyser.backend.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter — runs once per HTTP request.
 *
 * Intercepts every incoming request, checks for a Bearer token in the
 * Authorization header, validates it using JwtUtil, and if valid, sets
 * the authenticated principal in the Spring Security context.
 *
 * Once the SecurityContext is populated, downstream controllers can access
 * the authenticated user via @AuthenticationPrincipal or SecurityContextHolder.
 *
 * Extends OncePerRequestFilter to guarantee this filter runs exactly once
 * per request, even in async dispatch or forward scenarios.
 *
 * Registered in SecurityConfig via:
 *   http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 1. Skip filter if no Authorization header or it's not a Bearer token.
        //    Public routes (e.g. GET /api/problems, POST /api/auth/login) pass through here
        //    and are permitted by SecurityConfig's permitAll() rules downstream.
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the raw JWT from the header (strip "Bearer " prefix)
        final String jwt = authHeader.substring(BEARER_PREFIX.length());

        // 3. Quick structural validation before any DB call.
        //    Rejects expired, malformed, or tampered tokens immediately.
        if (!jwtUtil.isTokenStructureValid(jwt)) {
            log.warn("Invalid JWT structure on request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extract the username from the validated token
        final String username = jwtUtil.extractUsername(jwt);

        // 5. Only authenticate if:
        //    - A username was found in the token
        //    - No authentication is already set in the SecurityContext
        //      (prevents overwriting auth set by an earlier filter)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load the full UserDetails from the DB to get roles and verify the user still exists
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // 7. Final token validation — checks username match and expiry
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {

                // 8. Build the authentication token Spring Security understands
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // credentials null — we use JWT, not password
                                userDetails.getAuthorities() // empty list for now; add roles later here
                        );

                // 9. Attach request metadata (IP, session ID) to the authentication object
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Store in the SecurityContext — from this point on, the request
                //     is considered authenticated for the rest of the filter chain
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated user '{}' for request to: {}",
                        username, request.getRequestURI());

            } else {
                log.warn("JWT validation failed for user '{}' on request to: {}",
                        username, request.getRequestURI());
            }
        }

        // 11. Continue the filter chain regardless — SecurityConfig's route rules
        //     will reject the request if authentication wasn't set above
        filterChain.doFilter(request, response);
    }

    /**
     * Skip this filter entirely for public auth endpoints.
     * Avoids unnecessary JWT parsing overhead on login and register requests
     * where no token is present or expected.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/");
    }
}