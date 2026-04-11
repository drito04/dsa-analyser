package com.dsaanalyser.backend.repository;

import com.dsaanalyser.backend.model.AnalysisResult;
import com.dsaanalyser.backend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    Optional<AnalysisResult> findBySubmission(Submission submission);

    boolean existsBySubmission(Submission submission);

    void deleteBySubmission(Submission submission);
}