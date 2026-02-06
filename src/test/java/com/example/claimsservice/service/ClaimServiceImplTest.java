package com.example.claimsservice.service;


import com.example.claimsservice.audit.ClaimAuditService;
import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.dto.Policy;
import com.example.claimsservice.entity.enums.ClaimStatus;
import com.example.claimsservice.entity.enums.ClaimType;
import com.example.claimsservice.entity.enums.PolicyStatus;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.exception.*;
import com.example.claimsservice.mapper.ClaimMapper;
import com.example.claimsservice.repository.ClaimRepository;
import com.example.claimsservice.repository.PolicyRepository;
import com.example.claimsservice.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ClaimMapper claimMapper;

    @Mock
    private ClaimAuditService auditService;

    @InjectMocks
    private ClaimServiceImpl claimService;



    private Policy activePolicy;
    private ClaimResponse mockResponse;

    @BeforeEach
    void setup() {
        activePolicy = new Policy();
        activePolicy.setPolicyId(1L);
        activePolicy.setPolicyStatus(PolicyStatus.ACTIVE);

        mockResponse = ClaimResponse.builder()
            .claimId(100L)
            .policyId(1L)
            .claimNumber("CLM-001")
            .claimStatus(ClaimStatus.SUBMITTED)
            .claimAmount(BigDecimal.valueOf(5000))
            .approvedAmount(null)
            .build();
    }

    @Test
    void shouldCreateClaimSuccessfully() {
        CreateClaimRequest request = new CreateClaimRequest();
        request.setPolicyId(1L);
        request.setClaimAmount(BigDecimal.valueOf(5000));
        request.setClaimType(ClaimType.HOSPITALIZATION);

        Claim claim = new Claim();
        claim.setClaimId(100L);
        claim.setClaimAmount(BigDecimal.valueOf(5000));

        ClaimResponse response = mockResponse;
        response.setClaimId(100L);

        when(policyRepository.findByPolicyIdAndPolicyStatus(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(activePolicy));

        when(claimMapper.toEntity(request)).thenReturn(claim);
        when(claimRepository.save(Mockito.any())).thenReturn(claim);
        when(claimMapper.toResponse(Mockito.any())).thenReturn(response);

        ClaimResponse result = claimService.createClaim(request);

        assertNotNull(result);
        Assertions.assertEquals(100L, result.getClaimId());
        Mockito.verify(claimRepository).save(Mockito.any());
    }

    @Test
    void shouldThrowWhenPolicyNotActive() {
        Policy policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyStatus(PolicyStatus.INACTIVE);

        CreateClaimRequest request = new CreateClaimRequest();
        request.setPolicyId(1L);

        PolicyNotActiveException ex = assertThrows(
                PolicyNotActiveException.class,
                () -> claimService.createClaim(request)
        );

        Assertions.assertTrue(ex.getMessage().contains("Policy is not ACTIVE"));
    }

    @Test
    void shouldThrowBusinessExceptionWhenAmountInvalid() {
        CreateClaimRequest request = new CreateClaimRequest();
        request.setPolicyId(1L);
        request.setClaimAmount(BigDecimal.ZERO);
        request.setClaimType(ClaimType.HOSPITALIZATION);
        request.setDescription("Description");

        Claim claim = new Claim();
        claim.setClaimId(100L);
        claim.setClaimAmount(BigDecimal.ZERO);

        when(policyRepository.findByPolicyIdAndPolicyStatus(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(activePolicy));
        when(claimMapper.toEntity(request)).thenReturn(claim);

        assertThrows(InvalidClaimAmountException.class, () -> claimService.createClaim(request)
        );
    }

    @Test
    void shouldApproveClaimSuccessfully() {
        Claim claim = new Claim();
        claim.setClaimId(1L);
        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimAmount(BigDecimal.valueOf(5000));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);
        request.setApprovedAmount(BigDecimal.valueOf(4000));

        when(claimRepository.findForUpdate(1L))
                .thenReturn(Optional.of(claim));

        when(claimMapper.toResponse(Mockito.any()))
                .thenReturn(mockResponse);

        ClaimResponse result =
                claimService.updateStatus(1L, request);

        assertNotNull(result);
        Mockito.verify(claimRepository).save(claim);
    }

    @Test
    void shouldThrowNotFoundWhenClaimMissing() {
        when(claimRepository.findForUpdate(1L))
                .thenReturn(Optional.empty());

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);

        assertThrows(ClaimNotFoundException.class,
                () -> claimService.updateStatus(1L, request));
    }

    @Test
    void shouldThrowInvalidStateWhenAlreadyApproved() {
        Claim claim = new Claim();
        claim.setClaimStatus(ClaimStatus.APPROVED);

        when(claimRepository.findForUpdate(1L))
                .thenReturn(Optional.of(claim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.REJECTED);

        assertThrows(InvalidStatusTransitionException.class,
                () -> claimService.updateStatus(1L, request));
    }

    @Test
    void shouldThrowBusinessExceptionWhenApprovedAmountTooHigh() {
        Claim claim = new Claim();
        claim.setClaimStatus(ClaimStatus.SUBMITTED);
        claim.setClaimAmount(BigDecimal.valueOf(5000));

        when(claimRepository.findForUpdate(1L))
                .thenReturn(Optional.of(claim));

        UpdateClaimStatusRequest request = new UpdateClaimStatusRequest();
        request.setNewStatus(ClaimStatus.APPROVED);
        request.setApprovedAmount(BigDecimal.valueOf(7000));

        assertThrows(BusinessException.class,
                () -> claimService.updateStatus(1L, request));
    }
}