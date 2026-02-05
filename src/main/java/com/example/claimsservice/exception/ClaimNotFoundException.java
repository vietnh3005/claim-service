package com.example.claimsservice.exception;

import org.springframework.http.HttpStatus;

public class ClaimNotFoundException extends BusinessException {

    public ClaimNotFoundException(Long id) {
        super("Claim not found: " + id,
                HttpStatus.NOT_FOUND,
                "CLAIM_NOT_FOUND");
    }
}