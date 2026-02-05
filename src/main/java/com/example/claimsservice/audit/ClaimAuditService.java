package com.example.claimsservice.audit;

import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.dto.ClaimStatusHistory;
import com.example.claimsservice.entity.enums.ClaimStatus;
import com.example.claimsservice.repository.ClaimHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimAuditService {

    private final ClaimHistoryRepository repository;

    public void logStatusChange(Claim claim,
                                ClaimStatus oldStatus,
                                String note) {

        ClaimStatusHistory history = new ClaimStatusHistory(
                null,
                claim.getClaimId(),
                oldStatus.name(),
                claim.getClaimStatus().name(),
                note,
                Instant.now()
        );

        repository.save(history);

        log.info("Audit recorded for claim {} status change {} -> {}",
                claim.getClaimId(),
                oldStatus,
                claim.getClaimStatus());
    }
}