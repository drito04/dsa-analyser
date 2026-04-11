package com.dsaanalyser.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Binds the Gemini API key from application.properties into a Spring-managed bean.
 *
 * application.properties entry:
 *   gemini.api.key=${GEMINI_API_KEY}
 *
 * The actual key is stored in the .env file and injected at runtime —
 * never hardcoded or committed to Git.
 *
 * Injected into GeminiApiService via constructor injection (@RequiredArgsConstructor).
 */
@Getter
@Configuration
public class GeminiApiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Base URL for the Gemini REST API.
     * Centralised here so it can be changed in one place if Google
     * updates the endpoint or you switch to a different model version.
     */
    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    /**
     * Gemini model name to use for analysis.
     * Defaults to gemini-1.5-flash — fast and cost-effective for structured output.
     * Override in application.properties with: gemini.model=gemini-1.5-pro
     */
    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    /**
     * Maximum tokens Gemini is allowed to return per analysis response.
     * 1024 is sufficient for complexity + feedback + 3 hints.
     * Increase if you add more structured fields to the prompt output.
     */
    @Value("${gemini.max-output-tokens:1024}")
    private int maxOutputTokens;

    /**
     * Temperature controls randomness of Gemini's output.
     * 0.3 keeps responses deterministic and well-structured — important
     * for reliable JSON parsing in GeminiApiService.
     */
    @Value("${gemini.temperature:0.3}")
    private double temperature;
}