package org.example.businessservice.dto;

import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;
import org.example.businessservice.model.DeletionRequestStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessDtoTest {

    @Test
    void businessProfileResponseStoresAllFields() {
        LocalDateTime now = LocalDateTime.now();

        var response = new BusinessProfileResponse(
                1L,
                10L,
                "Test Business",
                "REG-001",
                BusinessType.PRIVATE_LIMITED,
                "Technology",
                "Test Street",
                "Hyderabad",
                "Telangana",
                "500001",
                "India",
                "test@example.com",
                "9876543210",
                new BigDecimal("1000000"),
                50,
                LocalDate.of(2020, 1, 1),
                BusinessStatus.ACTIVE,
                now,
                now
        );

        assertEquals(1L, response.id());
        assertEquals(10L, response.ownerId());
        assertEquals("Test Business", response.businessName());
        assertEquals("REG-001", response.registrationNumber());
        assertEquals(BusinessType.PRIVATE_LIMITED, response.businessType());
        assertEquals("Technology", response.industry());
        assertEquals("Test Street", response.address());
        assertEquals("Hyderabad", response.city());
        assertEquals("Telangana", response.state());
        assertEquals("500001", response.postalCode());
        assertEquals("India", response.country());
        assertEquals("test@example.com", response.contactEmail());
        assertEquals("9876543210", response.contactPhone());
        assertEquals(new BigDecimal("1000000"), response.annualRevenue());
        assertEquals(50, response.employeeCount());
        assertEquals(LocalDate.of(2020, 1, 1), response.establishedDate());
        assertEquals(BusinessStatus.ACTIVE, response.status());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }

    @Test
    void businessDeletionRequestResponseStoresAllFields() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime reviewed = created.plusDays(1);

        var response = new BusinessDeletionRequestResponse(
                1L,
                2L,
                10L,
                20L,
                "Closing business",
                DeletionRequestStatus.APPROVED,
                created,
                reviewed
        );

        assertEquals(1L, response.id());
        assertEquals(2L, response.businessId());
        assertEquals(10L, response.ownerId());
        assertEquals(20L, response.reviewerId());
        assertEquals("Closing business", response.reason());
        assertEquals(DeletionRequestStatus.APPROVED, response.status());
        assertEquals(created, response.createdAt());
        assertEquals(reviewed, response.reviewedAt());
    }

    @Test
    void businessSummaryResponseStoresAllFields() {
        var response = new BusinessSummaryResponse(
                1L,
                10L,
                "Test Business",
                "REG-001",
                BusinessType.LLP,
                "Finance",
                BusinessStatus.ACTIVE
        );

        assertEquals(1L, response.id());
        assertEquals(10L, response.ownerId());
        assertEquals("Test Business", response.businessName());
        assertEquals("REG-001", response.registrationNumber());
        assertEquals(BusinessType.LLP, response.businessType());
        assertEquals("Finance", response.industry());
        assertEquals(BusinessStatus.ACTIVE, response.status());
    }

    @Test
    void createBusinessProfileRequestStoresAllFields() {
        var request = new CreateBusinessProfileRequest(
                "Test Business",
                "REG-001",
                BusinessType.PRIVATE_LIMITED,
                "Technology",
                "Test Street",
                "Hyderabad",
                "Telangana",
                "500001",
                "India",
                "test@example.com",
                "9876543210",
                new BigDecimal("1000000"),
                50,
                LocalDate.of(2020, 1, 1)
        );

        assertEquals("Test Business", request.businessName());
        assertEquals("REG-001", request.registrationNumber());
        assertEquals(BusinessType.PRIVATE_LIMITED, request.businessType());
        assertEquals("Technology", request.industry());
        assertEquals("Test Street", request.address());
        assertEquals("Hyderabad", request.city());
        assertEquals("Telangana", request.state());
        assertEquals("500001", request.postalCode());
        assertEquals("India", request.country());
        assertEquals("test@example.com", request.contactEmail());
        assertEquals("9876543210", request.contactPhone());
        assertEquals(new BigDecimal("1000000"), request.annualRevenue());
        assertEquals(50, request.employeeCount());
        assertEquals(LocalDate.of(2020, 1, 1), request.establishedDate());
    }

    @Test
    void updateBusinessProfileRequestStoresAllFields() {
        var request = new UpdateBusinessProfileRequest(
                "Updated Business",
                BusinessType.LLP,
                "Finance",
                "Updated Street",
                "Hyderabad",
                "Telangana",
                "500002",
                "India",
                "updated@example.com",
                "9876543210",
                new BigDecimal("2000000"),
                100,
                LocalDate.of(2019, 1, 1)
        );

        assertEquals("Updated Business", request.businessName());
        assertEquals(BusinessType.LLP, request.businessType());
        assertEquals("Finance", request.industry());
        assertEquals("Updated Street", request.address());
        assertEquals("Hyderabad", request.city());
        assertEquals("Telangana", request.state());
        assertEquals("500002", request.postalCode());
        assertEquals("India", request.country());
        assertEquals("updated@example.com", request.contactEmail());
        assertEquals("9876543210", request.contactPhone());
        assertEquals(new BigDecimal("2000000"), request.annualRevenue());
        assertEquals(100, request.employeeCount());
        assertEquals(LocalDate.of(2019, 1, 1), request.establishedDate());
    }
}