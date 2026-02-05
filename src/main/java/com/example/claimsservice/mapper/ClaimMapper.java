package com.example.claimsservice.mapper;

import com.example.claimsservice.entity.dto.Claim;
import com.example.claimsservice.entity.request.CreateClaimRequest;
import com.example.claimsservice.entity.response.ClaimResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    Claim toEntity(CreateClaimRequest request);

    ClaimResponse toResponse(Claim claim);
}