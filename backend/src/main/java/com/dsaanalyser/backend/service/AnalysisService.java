package com.dsaanalyser.backend.service;

import com.dsaanalyser.backend.dto.SubmissionRequest;
import com.dsaanalyser.backend.dto.AnalysisResponse;
import com.dsaanalyser.backend.dto.SubmissionRequest;
import com.dsaanalyser.backend.exception.ProblemNotFoundException;
import com.dsaanalyser.backend.model.Problem;
import com.dsaanalyser.backend.model.Submission;
import com.dsaanalyser.backend.model.User;
import com.dsaanalyser.backend.repository.ProblemRepository;
import com.dsaanalyser.backend.repository.SubmissionRepository;
import com.dsaanalyser.backend.repository.UserRepository;
import com.dsaanalyser.backend.service.GeminiApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.UnknownNullability;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final GeminiApiService geminiApiService;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnalysisResponse analyzeCode(@Valid @UnknownNullability SubmissionRequest request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ProblemNotFoundException(
                        "Problem not found with id: " + request.getProblemId()));

        log.info("Analyzing submission for user='{}', problem='{}', language='{}'",
                username, problem.getTitle(), request.getLanguage());

        AnalysisResponse analysisResponse = geminiApiService.analyzeCode(
                problem.getTitle(),
                problem.getDescription(),
                request.getCode(),
                request.getLanguage()
        );

        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .code(request.getCode())
                .language(request.getLanguage())
                .timeComplexity(analysisResponse.getTimeComplexity())
                .spaceComplexity(analysisResponse.getSpaceComplexity())
                .feedback(analysisResponse.getFeedback())
                .submittedAt(LocalDateTime.now())
                .build();

        submissionRepository.save(submission);
        log.info("Submission saved with id={}", submission.getId());

        return analysisResponse;
    }

    public List<AnalysisResponse> getSubmissionsByUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Submission> submissions =
                submissionRepository.findByUserOrderBySubmittedAtDesc(user);

        return submissions.stream()
                .map(this::mapSubmissionToResponse)
                .collect(Collectors.toList());
    }

    private AnalysisResponse mapSubmissionToResponse(Submission submission) {
        return AnalysisResponse.builder()
                .submissionId(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .language(submission.getLanguage())
                .code(submission.getCode())
                .timeComplexity(submission.getTimeComplexity())
                .spaceComplexity(submission.getSpaceComplexity())
                .feedback(submission.getFeedback())
                .submittedAt(submission.getSubmittedAt())
                .hints(null)
                .isOptimal(null)
                .build();
    }
}