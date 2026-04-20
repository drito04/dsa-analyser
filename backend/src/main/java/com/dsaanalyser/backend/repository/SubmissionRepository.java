package com.dsaanalyser.backend.repository;

import com.dsaanalyser.backend.model.Problem;
import com.dsaanalyser.backend.model.Submission;
import com.dsaanalyser.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserOrderBySubmittedAtDesc(User user);


    List<Submission> findByUserAndProblemOrderBySubmittedAtDesc(User user, Problem problem);

    Optional<Submission> findTopByUserAndProblemOrderBySubmittedAtDesc(User user, Problem problem);

    long countByUserAndProblem(User user, Problem problem);

    long countByProblem(Problem problem);

    @Query("SELECT DISTINCT s.problem FROM Submission s WHERE s.user = :user")
    List<Problem> findDistinctProblemsByUser(@Param("user") User user);

    @Query("""
            SELECT s FROM Submission s
            JOIN AnalysisResult ar ON ar.submission = s
            WHERE s.user = :user AND ar.isOptimal = true
            ORDER BY s.submittedAt DESC
            """)
    List<Submission> findOptimalSubmissionsByUser(@Param("user") User user);
}