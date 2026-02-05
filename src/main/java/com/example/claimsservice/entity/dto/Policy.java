package com.example.claimsservice.entity.dto;

import com.example.claimsservice.entity.enums.PolicyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "POLICY")
@Getter
@Setter
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_seq")
    @SequenceGenerator(name = "policy_seq", sequenceName = "POLICY_SEQ", allocationSize = 1)
    private Long policyId;

    private String policyNumber;

    @Enumerated(EnumType.STRING)
    private PolicyStatus policyStatus;

    private LocalDate effectiveDate;
    private LocalDate expiryDate;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    private List<Claim> claims;
}
