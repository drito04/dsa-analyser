package com.dsaanalyser.backend.service;

import com.dsaanalyser.backend.config.GeminiApiConfig;
import com.dsaanalyser.backend.dto.AnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiService {

    private final WebClient webClient;
    private final GeminiApiConfig geminiApiConfig;
    private final ObjectMapper objectMapper;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public AnalysisResponse analyzeCode(String problemTitle,
                                        String problemDescription,
                                        String userCode,
                                        String language) {

        String prompt = buildPrompt(problemTitle, problemDescription, userCode, language);

        Map<String, Object> requestBody = buildRequestBody(prompt);

        String rawResponse = webClient.post()
                .uri(GEMINI_API_URL + "?key=" + geminiApiConfig.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new RuntimeException("Gemini API client error: " + clientResponse.statusCode())))
                .onStatus(status -> status.is5xxServerError(), clientResponse ->
                        Mono.error(new RuntimeException("Gemini API server error: " + clientResponse.statusCode())))
                .bodyToMono(String.class)
                .block(); // Blocking here to keep service layer simple; swap to reactive chain if needed

        return parseGeminiResponse(rawResponse);
    }

    private String buildPrompt(String problemTitle,
                               String problemDescription,
                               String userCode,
                               String language) {
        return """
                You are an expert DSA (Data Structures and Algorithms) tutor.
                Analyze the code below and respond ONLY with a valid JSON object — no markdown, no explanation outside the JSON.

                Problem: %s
                Description: %s

                User's code (%s):
                %s

                Return this exact JSON structure:
                {
                  "timeComplexity": "<e.g. O(n^2)>",
                  "spaceComplexity": "<e.g. O(n)>",
                  "isOptimal": <true or false>,
                  "feedback": "<A concise paragraph explaining the current approach and its trade-offs>",
                  "hints": [
                    "<Hint 1 — guides the user toward the optimal approach WITHOUT revealing the answer>",
                    "<Hint 2 — a follow-up hint if needed>",
                    "<Hint 3 — optional deeper nudge>"
                  ],
                  "optimalTimeComplexity": "<e.g. O(n log n) — only fill if isOptimal is false, else null>",
                  "optimalSpaceComplexity": "<e.g. O(1) — only fill if isOptimal is false, else null>"
                }

                Rules:
                - Do NOT reveal the optimal solution's code or algorithm name directly.
                - Hints must be guiding questions or observations, not direct instructions.
                - If the solution is already optimal, set isOptimal to true and leave optimalTimeComplexity and optimalSpaceComplexity as null.
                """.formatted(problemTitle, problemDescription, language, userCode);
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.3,          // Lower temp = more deterministic, better for structured output
                        "maxOutputTokens", 1024,
                        "responseMimeType", "application/json"
                )
        );
    }

    private AnalysisResponse parseGeminiResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            // Gemini response structure: candidates[0].content.parts[0].text
            String jsonText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            JsonNode parsed = objectMapper.readTree(jsonText);

            List<String> hints = objectMapper.convertValue(
                    parsed.path("hints"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            return AnalysisResponse.builder()
                    .timeComplexity(parsed.path("timeComplexity").asText())
                    .spaceComplexity(parsed.path("spaceComplexity").asText())
                    .isOptimal(parsed.path("isOptimal").asBoolean())
                    .feedback(parsed.path("feedback").asText())
                    .hints(hints)
                    .optimalTimeComplexity(
                            parsed.path("optimalTimeComplexity").isNull()
                                    ? null : parsed.path("optimalTimeComplexity").asText())
                    .optimalSpaceComplexity(
                            parsed.path("optimalSpaceComplexity").isNull()
                                    ? null : parsed.path("optimalSpaceComplexity").asText())
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Gemini API response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse AI analysis response. Please try again.");
        }
    }
}