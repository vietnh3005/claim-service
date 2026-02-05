package com.example.claimsservice.repository;

import com.example.claimsservice.entity.dto.ClaimStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimHistoryRepository
        extends JpaRepository<ClaimStatusHistory, Long> {
}