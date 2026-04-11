package com.dsaanalyser.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for all JWT operations:
 *  - Generating signed tokens on login / register
 *  - Extracting claims (username, expiry) from a token
 *  - Validating tokens on every protected request
 *
 * Uses the JJWT library (io.jsonwebtoken) declared in pom.xml.
 * Algorithm: HMAC-SHA256 (HS256) — symmetric, fast, standard for JWTs.
 *
 * application.properties entries:
 *   jwt.secret=<your-256-bit-base64-encoded-secret>
 *   jwt.expiration=3600000   # 1 hour in milliseconds (as per project spec)
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Token validity in milliseconds.
     * Default: 3,600,000 ms = 1 hour (matches project spec).
     * Override in application.properties with: jwt.expiration=86400000 (24h)
     */
    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    // -------------------------------------------------------------------------
    // Token Generation
    // -------------------------------------------------------------------------

    /**
     * Generates a signed JWT for the given username.
     * Called by UserService on both register and login.
     *
     * Token payload includes:
     *  - sub  (subject)   → username
     *  - iat  (issued at) → current timestamp
     *  - exp  (expiry)    → current timestamp + jwtExpiration
     *
     * @param username The authenticated user's username
     * @return Signed JWT string
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    /**
     * Overloaded version that accepts extra claims for future extensibility.
     * e.g. adding a "role" claim when roles are introduced.
     *
     * @param extraClaims Additional claims to embed in the token payload
     * @param username    The authenticated user's username (subject)
     * @return Signed JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // -------------------------------------------------------------------------
    // Token Validation
    // -------------------------------------------------------------------------

    /**
     * Validates a JWT against the given username.
     * Called by JwtAuthFilter on every protected request.
     *
     * A token is valid when:
     *  1. The username in the token matches the loaded UserDetails
     *  2. The token has not expired
     *
     * @param token       The JWT string from the Authorization header
     * @param username    The username to verify against the token's subject
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    /**
     * Validates the JWT structure and signature only (without username check).
     * Used in JwtAuthFilter before loading UserDetails to fail fast on
     * malformed or tampered tokens.
     *
     * @param token The JWT string to validate
     * @return true if the token is structurally valid and signature is correct
     */
    public boolean isTokenStructureValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Claims Extraction
    // -------------------------------------------------------------------------

    /**
     * Extracts the username (subject claim) from the token.
     * The primary identifier used throughout the application.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the token expiration date.
     * Used internally to check if a token has expired.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor — accepts a function to pull any field from the Claims.
     * Keeps the above methods clean without duplicating parsing logic.
     *
     * @param token          The JWT string
     * @param claimsResolver Function that extracts a specific claim value
     * @param <T>            The return type of the claim value
     * @return The extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Derives the HMAC-SHA256 signing key from the configured secret string.
     * Keys.hmacShaKeyFor() ensures the key meets the minimum length requirement
     * for HS256 (256 bits / 32 bytes). If your jwt.secret is shorter than 32
     * bytes, JJWT will throw a WeakKeyException at startup — use a long secret.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}