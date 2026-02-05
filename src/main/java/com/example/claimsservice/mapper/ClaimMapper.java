package com.example.claimsservice.mapper;

import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {
    @Mapping(target = "policy.policyId", source = "policyId")
    Claim toEntity(CreateClaimRequest request);

    @Mapping(target = "policyId", source = "policy.policyId")
    ClaimResponse toResponse(Claim claim);
}