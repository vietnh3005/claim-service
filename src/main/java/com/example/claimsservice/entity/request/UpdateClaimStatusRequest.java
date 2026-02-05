package com.example.claimsservice.entity.request;

import com.example.claimsservice.entity.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateClaimStatusRequest {

    @NotNull
    private ClaimStatus newStatus;

    private BigDecimal approvedAmount;

    private String note;
}