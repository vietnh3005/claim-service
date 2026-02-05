package com.example.claimsservice.service;

import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;

public interface ClaimService {

    ClaimResponse createClaim(CreateClaimRequest request);

    ClaimResponse updateStatus(Long id, UpdateClaimStatusRequest request);

    ClaimResponse getClaim(Long id);
}