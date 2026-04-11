package com.dsaanalyser.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProblemNotFoundException extends RuntimeException {

    private final Long problemId;

    public ProblemNotFoundException(String message) {
        super(message);
        this.problemId = null;
    }

    public ProblemNotFoundException(Long problemId) {
        super("Problem not found with id: " + problemId);
        this.problemId = problemId;
    }

    public Long getProblemId() {
        return problemId;
    }
}