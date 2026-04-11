package com.dsaanalyser.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration allowing the React frontend to communicate
 * with the Spring Boot backend across different origins.
 *
 * Without this, browsers block all cross-origin requests by default,
 * meaning the frontend running on localhost:3000 (dev) or Vercel (prod)
 * would be unable to reach the API on localhost:8080 or Railway.
 *
 * Allowed origins are injected from application.properties so they
 * can differ between dev and production without code changes.
 *
 * application.properties entries:
 *   cors.allowed-origins=http://localhost:3000,https://your-app.vercel.app
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Only allow requests from the configured frontend origins.
                // In production this should be the exact Vercel deployment URL.
                .allowedOrigins(allowedOrigins)

                // Standard HTTP methods used by the REST API.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers — needed for the Authorization: Bearer <token> header.
                .allowedHeaders("*")

                // Allow the frontend to send cookies or Authorization headers
                // with cross-origin requests (required for JWT in headers).
                .allowCredentials(true)

                // Cache the preflight OPTIONS response for 1 hour (3600s)
                // to reduce the number of preflight requests the browser makes.
                .maxAge(3600);
    }
}