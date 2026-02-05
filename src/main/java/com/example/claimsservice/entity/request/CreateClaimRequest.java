package com.example.claimsservice.entity.request;

import com.example.claimsservice.entity.enums.ClaimType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateClaimRequest {

    @NotNull
    private Long policyId;

    @Positive
    private BigDecimal claimAmount;

    private ClaimType claimType;

    @Size(max = 500)
    private String description;
}