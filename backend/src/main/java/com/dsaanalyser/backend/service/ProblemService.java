package com.dsaanalyser.backend.service;

import com.dsaanalyser.backend.dto.ProblemDto;
import com.dsaanalyser.backend.exception.ProblemNotFoundException;
import com.dsaanalyser.backend.model.Problem;
import com.dsaanalyser.backend.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public List<ProblemDto> getAllProblems(String tag, String difficulty) {
        List<Problem> problems;

        if (tag != null && difficulty != null) {
            problems = problemRepository.findByTagsContainingIgnoreCaseAndDifficulty(
                    tag, Problem.Difficulty.valueOf(difficulty.toUpperCase()));
        } else if (tag != null) {
            problems = problemRepository.findByTagsContainingIgnoreCase(tag);
        } else if (difficulty != null) {
            problems = problemRepository.findByDifficulty(
                    Problem.Difficulty.valueOf(difficulty.toUpperCase()));
        } else {
            problems = problemRepository.findAll();
        }

        return problems.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProblemDto getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found with id: " + id));
        return mapToDto(problem);
    }

    private ProblemDto mapToDto(Problem problem) {
        return ProblemDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty().name())
                .tags(problem.getTags())
                .constraints(problem.getConstraints())
                .optimalComplexity(problem.getOptimalComplexity())
                .build();
    }
}