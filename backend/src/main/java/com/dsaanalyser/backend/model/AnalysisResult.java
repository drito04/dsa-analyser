package com.dsaanalyser.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for storing the extended AI analysis result linked to a Submission.
 *
 * While the Submission entity stores the core complexity fields (timeComplexity,
 * spaceComplexity, feedback) for quick history retrieval, AnalysisResult stores
 * the fuller Gemini output — isOptimal, optimalComplexities, and the hints list.
 *
 * This separation keeps the submissions table lean and fast for list queries,
 * while still allowing the full analysis to be retrieved when a user revisits
 * a specific submission in detail.
 *
 * Relationship: One Submission → One AnalysisResult (OneToOne, same lifecycle).
 */
@Entity
@Table(name = "analysis_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @Column(name = "is_optimal", nullable = false)
    private Boolean isOptimal;

    @Column(name = "optimal_time_complexity", length = 50)
    private String optimalTimeComplexity;

    @Column(name = "optimal_space_complexity", length = 50)
    private String optimalSpaceComplexity;

    @Column(name = "hints", columnDefinition = "TEXT")
    private String hints;
}