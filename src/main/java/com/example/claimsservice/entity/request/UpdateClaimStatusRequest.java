package com.example.claimsservice.entity.request;

import com.example.claimsservice.entity.ClaimStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateClaimStatusRequest {

    private ClaimStatus newStatus;

    private BigDecimal approvedAmount;

    private String note;
}
