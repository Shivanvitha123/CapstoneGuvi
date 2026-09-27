package org.example.underwritingpolicyservice.dto;

import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PolicyDtoTest {

    @Test
    void createPolicyRequest_fieldsAreAccessible() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);

        CreatePolicyRequest request = new CreatePolicyRequest(
                1L, "POL-001", "HEALTH",
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                start, end, "Test"
        );

        assertEquals(1L, request.businessId());
        assertEquals("POL-001", request.policyNumber());
        assertEquals("HEALTH", request.policyType());
        assertEquals(new BigDecimal("100000"), request.coverageAmount());
        assertEquals(new BigDecimal("5000"), request.premiumAmount());
        assertEquals(start, request.startDate());
        assertEquals(end, request.endDate());
        assertEquals("Test", request.description());
    }

    @Test
    void updatePolicyRequest_fieldsAreAccessible() {
        UpdatePolicyRequest request = new UpdatePolicyRequest(
                2L, "POL-002", "LIFE",
                new BigDecimal("200000"),
                new BigDecimal("7000"),
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                "Updated"
        );

        assertEquals(2L, request.businessId());
        assertEquals("POL-002", request.policyNumber());
        assertEquals("LIFE", request.policyType());
        assertEquals(new BigDecimal("200000"), request.coverageAmount());
        assertEquals(new BigDecimal("7000"), request.premiumAmount());
        assertNotNull(request.startDate());
        assertNotNull(request.endDate());
        assertEquals("Updated", request.description());
    }

    @Test
    void updatePolicyRequest_allowsNullFields() {
        UpdatePolicyRequest request = new UpdatePolicyRequest(
                null, null, null, null, null, null, null, null
        );

        assertNull(request.businessId());
        assertNull(request.policyNumber());
        assertNull(request.policyType());
        assertNull(request.coverageAmount());
        assertNull(request.premiumAmount());
        assertNull(request.startDate());
        assertNull(request.endDate());
        assertNull(request.description());
    }

    @Test
    void policyResponse_fieldsAreAccessible() {
        LocalDateTime now = LocalDateTime.now();

        PolicyResponse response = new PolicyResponse(
                1L, 2L, 3L, "POL-001", "HEALTH",
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                PolicyStatus.DRAFT,
                "Test policy",
                now, now
        );

        assertEquals(1L, response.id());
        assertEquals(2L, response.businessId());
        assertEquals(3L, response.ownerId());
        assertEquals("POL-001", response.policyNumber());
        assertEquals("HEALTH", response.policyType());
        assertEquals(new BigDecimal("100000"), response.coverageAmount());
        assertEquals(new BigDecimal("5000"), response.premiumAmount());
        assertEquals(PolicyStatus.DRAFT, response.status());
        assertEquals("Test policy", response.description());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }
}