package com.dsaanalyser.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Code cannot be empty")
    @Size(min = 10, max = 50000, message = "Code must be between 10 and 50,000 characters")
    private String code;

    @NotBlank(message = "Language is required")
    @Pattern(
            regexp = "^(java|python|javascript)$",
            message = "Language must be one of: java, python, javascript"
    )
    private String language;
}