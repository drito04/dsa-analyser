package com.dsaanalyser.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisResponse {

    private Long submissionId;

    private Long problemId;

    private String problemTitle;

    private String language;

    private String code;

    private LocalDateTime submittedAt;

    private String timeComplexity;

    private String spaceComplexity;

    private Boolean isOptimal;

    private String feedback;

    private List<String> hints;

    private String optimalTimeComplexity;

    private String optimalSpaceComplexity;
}