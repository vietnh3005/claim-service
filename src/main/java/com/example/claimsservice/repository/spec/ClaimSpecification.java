package com.example.claimsservice.repository.spec;

import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.enums.ClaimStatus;
import org.springframework.data.jpa.domain.Specification;

public class ClaimSpecification {
    public static Specification<Claim> filter(Long policyId, ClaimStatus status) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (policyId != null) {
                predicates.getExpressions().add(
                        cb.equal(root.get("policy").get("policyId"), policyId)
                );
            }

            if (status != null) {
                predicates.getExpressions().add(
                        cb.equal(root.get("claimStatus"), status)
                );
            }

            return predicates;
        };
    }
}