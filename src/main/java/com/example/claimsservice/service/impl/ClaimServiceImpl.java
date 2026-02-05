package com.example.claimsservice.service.impl;

import com.example.claimsservice.entity.ClaimStatus;
import com.example.claimsservice.entity.PolicyStatus;
import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.dto.ClaimStatusHistory;
import com.example.claimsservice.entity.dto.Policy;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.mapper.ClaimMapper;
import com.example.claimsservice.repository.ClaimHistoryRepository;
import com.example.claimsservice.repository.ClaimRepository;
import com.example.claimsservice.repository.PolicyRepository;
import com.example.claimsservice.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository historyRepository;
    private final PolicyRepository policyRepository;
    private final ClaimMapper mapper;

    @Override
    public ClaimResponse createClaim(CreateClaimRequest request) {

        Policy policy = policyRepository
                .findByPolicyIdAndPolicyStatus(
                        request.getPolicyId(),
                        PolicyStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new RuntimeException("Policy must be ACTIVE"));

        Claim claim = mapper.toEntity(request);
        claim.setPolicy(policy);

        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimDate(LocalDate.now());
        claim.setClaimNumber(generateNumber());

        claimRepository.save(claim);

        return mapper.toResponse(claim);
    }

    @Override
    public ClaimResponse updateStatus(Long id, UpdateClaimStatusRequest request) {

        Claim claim = claimRepository.findForUpdate(id)
                .orElseThrow();

        ClaimStatus old = claim.getClaimStatus();

        if (old != ClaimStatus.SUBMITTED)
            throw new RuntimeException("Terminal state");

        if (request.getNewStatus() == ClaimStatus.APPROVED) {
            claim.setApprovedAmount(request.getApprovedAmount());
        }

        claim.setClaimStatus(request.getNewStatus());

        historyRepository.save(new ClaimStatusHistory(
                null,
                claim.getClaimId(),
                old.name(),
                request.getNewStatus().name(),
                request.getNote(),
                Instant.now()
        ));

        return mapper.toResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaim(Long id) {
        return mapper.toResponse(
                claimRepository.findById(id).orElseThrow()
        );
    }

    private String generateNumber() {
        return "CLM-" + Year.now() + "-" + UUID.randomUUID().toString().substring(0,6);
    }
}