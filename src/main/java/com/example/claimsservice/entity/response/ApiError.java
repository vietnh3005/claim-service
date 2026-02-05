package com.example.claimsservice.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String errorCode;
    private String message;
    private Instant timestamp;
    private String path;
}