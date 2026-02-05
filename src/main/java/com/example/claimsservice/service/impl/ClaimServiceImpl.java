package com.example.claimsservice.service.impl;

import com.example.claimsservice.audit.ClaimAuditService;
import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.dto.Policy;
import com.example.claimsservice.entity.enums.ClaimStatus;
import com.example.claimsservice.entity.enums.PolicyStatus;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.exception.ClaimNotFoundException;
import com.example.claimsservice.exception.InvalidStatusTransitionException;
import com.example.claimsservice.exception.PolicyNotActiveException;
import com.example.claimsservice.mapper.ClaimMapper;
import com.example.claimsservice.repository.ClaimRepository;
import com.example.claimsservice.repository.PolicyRepository;
import com.example.claimsservice.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final ClaimMapper mapper;
    private final ClaimAuditService auditService;

    @Override
    public ClaimResponse createClaim(CreateClaimRequest request) {

        log.debug("Creating claim for policy {}", request.getPolicyId());

        Policy policy = policyRepository
                .findByPolicyIdAndPolicyStatus(
                        request.getPolicyId(),
                        PolicyStatus.ACTIVE
                )
                .orElseThrow(() -> new PolicyNotActiveException(request.getPolicyId()));

        Claim claim = mapper.toEntity(request);
        claim.setPolicy(policy);
        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimDate(LocalDate.now());
        claim.setClaimNumber(generateNumber());

        claimRepository.save(claim);

        log.info("Claim {} created", claim.getClaimId());

        return mapper.toResponse(claim);
    }

    @Override
    public ClaimResponse getClaim(Long id) {

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        return mapper.toResponse(claim);
    }

    @Override
    public ClaimResponse updateStatus(Long id, UpdateClaimStatusRequest request) {

        Claim claim = claimRepository.findForUpdate(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));

        ClaimStatus oldStatus = claim.getClaimStatus();

        if (oldStatus != ClaimStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException();
        }

        if (request.getNewStatus() == null) {
            throw new InvalidStatusTransitionException();
        }

        if (request.getNewStatus() == ClaimStatus.APPROVED) {

            if (request.getApprovedAmount() == null ||
                    request.getApprovedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidStatusTransitionException();
            }

            if (request.getApprovedAmount().compareTo(claim.getClaimAmount()) > 0) {
                throw new InvalidStatusTransitionException();
            }

            claim.setApprovedAmount(request.getApprovedAmount());
        }

        claim.setClaimStatus(request.getNewStatus());

        auditService.logStatusChange(claim, oldStatus, request.getNote());

        log.info("Claim {} status updated from {} to {}",
                id, oldStatus, claim.getClaimStatus());

        return mapper.toResponse(claim);
    }

    private String generateNumber() {
        return "CLM-" + Year.now() + "-" +
                UUID.randomUUID().toString().substring(0, 6);
    }
}