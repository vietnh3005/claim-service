package com.example.claimsservice.entity.response;

import com.example.claimsservice.entity.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClaimResponse {

    private Long claimId;
    private Long policyId;
    private String claimNumber;
    private ClaimStatus claimStatus;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
}
