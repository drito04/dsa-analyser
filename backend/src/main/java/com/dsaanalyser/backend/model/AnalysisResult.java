package com.dsaanalyser.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    // These fields support the "Intelligent Feedback" flow [cite: 9, 22]
    private String complexity;        // Time complexity detected (e.g., O(n)) [cite: 32, 56]
    private List<String> issues;      // Performance bottlenecks or architectural issues [cite: 7, 57]
    private String aiSuggestions;     // Optimization recommendations from Gemini [cite: 22, 34, 60]
    private int score;                // Efficiency-based metric for the leaderboard [cite: 19, 89]
}