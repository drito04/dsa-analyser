package com.dsaanalyser.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private int status;      // HTTP Status code (e.g., 404, 500)
    private String error;       // Type of error (e.g., "AI_SERVICE_UNAVAILABLE")
    private String message;     // Helpful description for the student

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}