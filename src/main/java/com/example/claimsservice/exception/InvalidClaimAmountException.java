package com.example.claimsservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidClaimAmountException extends BusinessException {

    public InvalidClaimAmountException() {
        super("Invalid claim amount",
                HttpStatus.BAD_REQUEST,
                "INVALID_CLAIM_AMOUNT");
    }
}
