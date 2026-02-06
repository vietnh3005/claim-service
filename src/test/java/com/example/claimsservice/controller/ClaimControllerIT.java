package com.example.claimsservice.controller;

import com.example.claimsservice.entity.dto.Policy;
import com.example.claimsservice.entity.enums.PolicyStatus;
import com.example.claimsservice.repository.PolicyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClaimControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyRepository policyRepository;

    @BeforeEach
    void setup() {
        Policy policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyStatus(PolicyStatus.ACTIVE);
        policy.setPolicyNumber("POL-TEST-001");

        policyRepository.save(policy);
    }

    @Test
    void shouldCreateClaim() throws Exception {
        String body = """
        {
          "policyId": 1,
          "claimAmount": 5000,
          "claimType": "HOSPITALIZATION",
          "description": "Test"
        }
        """;

        mockMvc.perform(post("/api/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId").value(1));
    }

    @Test
    void shouldListClaims() throws Exception {
        mockMvc.perform(get("/api/claims")
                        .param("policyId", "1")
                        .param("status", "SUBMITTED")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldApproveClaim() throws Exception {
        String body = """
    {
      "newStatus": "APPROVED",
      "approvedAmount": 4000,
      "note": "OK"
    }
    """;

        mockMvc.perform(patch("/api/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimStatus").value("APPROVED"));
    }
}