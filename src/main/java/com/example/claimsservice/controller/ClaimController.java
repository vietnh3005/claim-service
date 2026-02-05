package com.example.claimsservice.controller;

import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.request.UpdateClaimStatusRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import com.example.claimsservice.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService service;

    @PostMapping
    public ResponseEntity<ClaimResponse> create(
            @RequestBody @Valid CreateClaimRequest req
    ) {
        return ResponseEntity.status(201).body(service.createClaim(req));
    }

    @PatchMapping("/{id}")
    public ClaimResponse update(
            @PathVariable Long id,
            @RequestBody UpdateClaimStatusRequest req
    ) {
        return service.updateStatus(id, req);
    }

    @GetMapping("/{id}")
    public ClaimResponse get(@PathVariable Long id) {
        return service.getClaim(id);
    }
}
