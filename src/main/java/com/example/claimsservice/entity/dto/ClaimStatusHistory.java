package com.example.claimsservice.entity.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "CLAIM_STATUS_HISTORY")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClaimStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "CLAIM_HISTORY_SEQ", allocationSize = 1)
    private Long historyId;

    private Long claimId;

    private String oldStatus;

    private String newStatus;

    private String note;

    private Instant changedAt = Instant.now();
}