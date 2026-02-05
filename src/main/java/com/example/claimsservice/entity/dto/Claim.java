package com.example.claimsservice.entity.dto;

import com.example.claimsservice.entity.ClaimStatus;
import com.example.claimsservice.entity.ClaimType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "CLAIM")
@Getter
@Setter
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claim_seq")
    @SequenceGenerator(name = "claim_seq", sequenceName = "CLAIM_SEQ", allocationSize = 1)
    private Long claimId;

    @Column(unique = true)
    private String claimNumber;

    private Long policyId;

    private LocalDate claimDate;

    private BigDecimal claimAmount;

    private BigDecimal approvedAmount;

    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;

    @Enumerated(EnumType.STRING)
    private ClaimType claimType;

    private String description;

    @Version
    private Long versionNo;

    private Instant createdAt = Instant.now();
}