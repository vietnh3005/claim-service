package com.example.claimsservice.entity.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListClaimsResponse {
    private List<ClaimResponse> data;
    private long total;
    private int limit;
    private int offset;
}