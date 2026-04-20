package com.dsaanalyser.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDto {

    private Long id;

    private String title;

    private String description;

    private String difficulty;

    private String tags;

    private String constraints;

    private String optimalComplexity;
}