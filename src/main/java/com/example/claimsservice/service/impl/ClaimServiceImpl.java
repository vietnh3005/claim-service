package com.example.claimsservice.service.impl;

import com.example.claimsservice.audit.ClaimAuditService;
import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.dto.Policy;
import com.example.claimsservice.entity.enums.ClaimStatus;
import com.example.claimsservice.entity.enums.PolicyStatus;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.entity.response.ListClaimsResponse;
import com.example.claimsservice.exception.ClaimNotFoundException;
import com.example.claimsservice.exception.InvalidClaimAmountException;
import com.example.claimsservice.exception.InvalidStatusTransitionException;
import com.example.claimsservice.exception.PolicyNotActiveException;
import com.example.claimsservice.mapper.ClaimMapper;
import com.example.claimsservice.repository.ClaimRepository;
import com.example.claimsservice.repository.PolicyRepository;
import com.example.claimsservice.repository.spec.ClaimSpecification;
import com.example.claimsservice.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
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
        // Policy must be ACTIVE in order to create new Claim
        Policy policy = policyRepository
            .findByPolicyIdAndPolicyStatus(
                    request.getPolicyId(),
                    PolicyStatus.ACTIVE
            )
            .orElseThrow(() -> new PolicyNotActiveException(request.getPolicyId()));

        Claim claim = mapper.toEntity(request);

        //The ClaimAmount must greater than 0
        if (!(claim.getClaimAmount().longValue() >= 0)) {
            throw new InvalidClaimAmountException();
        }

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

        // Only allow transitions: SUBMITTED → APPROVED or SUBMITTED → REJECTED
        // Cannot transition from APPROVED or REJECTED
        if (oldStatus != ClaimStatus.SUBMITTED) {
            throw new InvalidStatusTransitionException();
        }

        if (request.getNewStatus() == null) {
            throw new InvalidStatusTransitionException();
        }

        //  If newStatus is APPROVED, approvedAmount must be provided and > 0
        //  approvedAmount cannot exceed claimAmount
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

        // log changes to history table
        auditService.logStatusChange(claim, oldStatus, request.getNote());

        log.info("Claim {} status updated from {} to {}",
                id, oldStatus, claim.getClaimStatus());

        return mapper.toResponse(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ListClaimsResponse listClaims(
            Long policyId,
            ClaimStatus status,
            int limit,
            int offset
    ) {
        log.info("Listing claims policyId={}, status={}, limit={}, offset={}",
                policyId, status, limit, offset);

        limit = Math.min(limit <= 0 ? 20 : limit, 100);
        offset = Math.max(offset, 0);

        Pageable pageable = PageRequest.of(offset / limit, limit,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Claim> spec = ClaimSpecification.filter(policyId, status);

        Page<Claim> page = claimRepository.findAll(spec, pageable);

        List<ClaimResponse> responses = page.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ListClaimsResponse.builder()
                .data(responses)
                .total(page.getTotalElements())
                .limit(limit)
                .offset(offset)
                .build();
    }

    private String generateNumber() {
        return "CLM-" + Year.now() + "-" +
                UUID.randomUUID().toString().substring(0, 6);
    }
}