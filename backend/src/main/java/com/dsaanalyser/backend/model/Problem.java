package com.dsaanalyser.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Remove [cite: 85]

    private String title; // Remove [cite: 86]

    @Column(columnDefinition = "TEXT")
    private String description; // Remove [cite: 86]

    private String difficulty; // Easy, Medium, Hard

    @Column(name = "expected_complexity")
    private String expectedComplexity;
}