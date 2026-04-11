package com.dsaanalyser.backend.controller;

import com.dsaanalyser.backend.dto.ProblemDto;
import com.dsaanalyser.backend.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProblems(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String difficulty) {

        List<ProblemDto> problems = problemService.getAllProblems(tag, difficulty);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", problems);
        response.put("message", "Problems fetched successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProblemById(@PathVariable Long id) {

        ProblemDto problem = problemService.getProblemById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", problem);
        response.put("message", "Problem fetched successfully");

        return ResponseEntity.ok(response);
    }
}