package com.dsaanalyser.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problems")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @Column(length = 255)
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "optimal_complexity", length = 50)
    private String optimalComplexity;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}