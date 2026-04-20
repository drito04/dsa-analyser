package com.dsaanalyser.backend.controller;

import com.dsaanalyser.backend.dto.AnalysisResponse;
import com.dsaanalyser.backend.dto.SubmissionRequest;
import com.dsaanalyser.backend.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/api/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSubmission(
            @Valid @RequestBody SubmissionRequest submissionRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        AnalysisResponse analysisResponse = analysisService.analyzeCode(submissionRequest, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", analysisResponse);
        response.put("message", "Code analyzed successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/submissions/me")
    public ResponseEntity<Map<String, Object>> getMySubmissions(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        List<AnalysisResponse> submissions = analysisService.getSubmissionsByUser(username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", submissions);
        response.put("message", "Submission history fetched successfully");

        return ResponseEntity.ok(response);
    }
}