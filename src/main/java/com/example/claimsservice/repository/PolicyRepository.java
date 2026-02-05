package com.example.claimsservice.repository;

import com.example.claimsservice.entity.enums.PolicyStatus;
import com.example.claimsservice.entity.dto.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyIdAndPolicyStatus(
            Long id,
            PolicyStatus status
    );

    boolean existsByPolicyId(Long id);
}