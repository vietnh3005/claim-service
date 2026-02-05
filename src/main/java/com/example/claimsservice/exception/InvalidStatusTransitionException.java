package com.example.claimsservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException() {
        super("Invalid claim status transition",
                HttpStatus.BAD_REQUEST,
                "INVALID_STATUS_TRANSITION");
    }
}