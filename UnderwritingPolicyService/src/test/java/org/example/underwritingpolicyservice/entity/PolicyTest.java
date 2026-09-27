package org.example.underwritingpolicyservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PolicyTest {

    @Test
    void builderCreatesPolicy() {
        LocalDateTime now = LocalDateTime.now();

        Policy policy = Policy.builder()
                .id(1L)
                .businessId(2L)
                .ownerId(3L)
                .policyNumber("POL-001")
                .policyType("HEALTH")
                .coverageAmount(new BigDecimal("100000"))
                .premiumAmount(new BigDecimal("5000"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status("DRAFT")
                .description("Test policy")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, policy.getId());
        assertEquals(2L, policy.getBusinessId());
        assertEquals(3L, policy.getOwnerId());
        assertEquals("POL-001", policy.getPolicyNumber());
        assertEquals("HEALTH", policy.getPolicyType());
        assertEquals(new BigDecimal("100000"), policy.getCoverageAmount());
        assertEquals(new BigDecimal("5000"), policy.getPremiumAmount());
        assertEquals("DRAFT", policy.getStatus());
        assertEquals("Test policy", policy.getDescription());
        assertEquals(now, policy.getCreatedAt());
        assertEquals(now, policy.getUpdatedAt());
    }

    @Test
    void settersUpdatePolicyFields() {
        Policy policy = new Policy();

        policy.setId(1L);
        policy.setBusinessId(2L);
        policy.setOwnerId(3L);
        policy.setPolicyNumber("POL-002");
        policy.setPolicyType("LIFE");
        policy.setCoverageAmount(new BigDecimal("200000"));
        policy.setPremiumAmount(new BigDecimal("7000"));
        policy.setStatus("SUBMITTED");
        policy.setDescription("Updated");

        assertEquals(1L, policy.getId());
        assertEquals(2L, policy.getBusinessId());
        assertEquals(3L, policy.getOwnerId());
        assertEquals("POL-002", policy.getPolicyNumber());
        assertEquals("LIFE", policy.getPolicyType());
        assertEquals(new BigDecimal("200000"), policy.getCoverageAmount());
        assertEquals(new BigDecimal("7000"), policy.getPremiumAmount());
        assertEquals("SUBMITTED", policy.getStatus());
        assertEquals("Updated", policy.getDescription());
    }

    @Test
    void noArgsConstructorCreatesPolicy() {
        Policy policy = new Policy();

        assertNotNull(policy);
        assertNull(policy.getId());
        assertNull(policy.getPolicyNumber());
    }
}