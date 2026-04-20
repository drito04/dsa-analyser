package com.dsaanalyser.backend.repository;

import com.dsaanalyser.backend.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the `problems` table.
 *
 * Spring Data JPA generates all query implementations at runtime —
 * no SQL or boilerplate needed. Method names follow the Spring
 * naming convention so queries are derived automatically.
 */
@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByDifficulty(Problem.Difficulty difficulty);

    List<Problem> findByTagsContainingIgnoreCase(String tag);

    List<Problem> findByTagsContainingIgnoreCaseAndDifficulty(
            String tag,
            Problem.Difficulty difficulty
    );

    @Query("SELECT p.difficulty, COUNT(p) FROM Problem p GROUP BY p.difficulty")
    List<Object[]> countByDifficulty();

    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Problem> searchByTitle(@Param("keyword") String keyword);
}