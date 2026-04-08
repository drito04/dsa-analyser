package com.dsaanalyser.backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AIService {

    private final WebClient webClient;
    private final String apiKey;

    public AIService(WebClient.Builder webClientBuilder,
                     @Value("${gemini.api.url}") String baseUrl,
                     @Value("${gemini.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build() ;
        this.apiKey = apiKey;
    }

    public ProblemAnalysis generateAnalysis(String question, String answer) {
        String prompt = buildPrompt(resumeText, jobDescription);

        String safeContent = prompt
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        String requestBody = String.format("""
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]  
                    }]
                }""", safeContent);

        // Send Request
        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .build())
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Extract Response
        return extractResponseContent(response);
    }

}
