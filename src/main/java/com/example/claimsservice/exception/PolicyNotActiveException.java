package com.example.claimsservice.exception;

import org.springframework.http.HttpStatus;

public class PolicyNotActiveException extends BusinessException {

    public PolicyNotActiveException(Long id) {
        super("Policy is not ACTIVE: " + id,
                HttpStatus.CONFLICT,
                "POLICY_NOT_ACTIVE");
    }
}
