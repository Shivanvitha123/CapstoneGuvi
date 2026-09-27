package org.example.businessservice.dto;

import org.example.businessservice.model.BusinessType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UpdateBusinessProfileRequestTest {

    @Test
    void shouldCreateAndReadRequest() {
        UpdateBusinessProfileRequest request =
                new UpdateBusinessProfileRequest(
                        "Updated Business",
                        BusinessType.values()[0],
                        "Technology",
                        "Updated Address",
                        "Hyderabad",
                        "Telangana",
                        "500001",
                        "India",
                        "updated@example.com",
                        "9876543210",
                        new BigDecimal("200000"),
                        30,
                        LocalDate.of(2020, 1, 1)
                );

        assertEquals("Updated Business", request.businessName());
        assertEquals(BusinessType.values()[0], request.businessType());
        assertEquals("Technology", request.industry());
        assertEquals("Updated Address", request.address());
        assertEquals("Hyderabad", request.city());
        assertEquals("Telangana", request.state());
        assertEquals("500001", request.postalCode());
        assertEquals("India", request.country());
        assertEquals("updated@example.com", request.contactEmail());
        assertEquals("9876543210", request.contactPhone());
        assertEquals(new BigDecimal("200000"), request.annualRevenue());
        assertEquals(30, request.employeeCount());
        assertEquals(LocalDate.of(2020, 1, 1), request.establishedDate());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {
        UpdateBusinessProfileRequest first =
                new UpdateBusinessProfileRequest(
                        "Test", BusinessType.values()[0],
                        "IT", "Address", "City", "State",
                        "500001", "India", "test@example.com",
                        "9876543210", BigDecimal.TEN, 10,
                        LocalDate.of(2020, 1, 1)
                );

        UpdateBusinessProfileRequest second =
                new UpdateBusinessProfileRequest(
                        "Test", BusinessType.values()[0],
                        "IT", "Address", "City", "State",
                        "500001", "India", "test@example.com",
                        "9876543210", BigDecimal.TEN, 10,
                        LocalDate.of(2020, 1, 1)
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("Test"));
    }
}