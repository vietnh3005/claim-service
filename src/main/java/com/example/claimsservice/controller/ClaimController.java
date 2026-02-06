package com.example.claimsservice.controller;

import com.example.claimsservice.entity.enums.ClaimStatus;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.entity.response.ListClaimsResponse;
import com.example.claimsservice.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService service;

    @PostMapping
    public ResponseEntity<ClaimResponse> createClaim(
            @Valid @RequestBody CreateClaimRequest request
    ) {

        log.info("Received create claim request");

        ClaimResponse response = service.createClaim(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ClaimResponse getClaim(@PathVariable Long id) {

        log.debug("Fetching claim {}", id);

        return service.getClaim(id);
    }

    @GetMapping
    public ResponseEntity<ListClaimsResponse> listClaims(
            @RequestParam(required = false) Long policyId,
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(
                service.listClaims(policyId, status, limit, offset)
        );
    }

    @PatchMapping("/{id}")
    public ClaimResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClaimStatusRequest request
    ) {

        log.info("Updating claim {} status", id);

        return service.updateStatus(id, request);
    }
}