package org.example.businessservice.dto;

import org.example.businessservice.model.BusinessType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreateBusinessProfileRequestTest {

    @Test
    void shouldCreateAndReadRequest() {
        CreateBusinessProfileRequest request =
                new CreateBusinessProfileRequest(
                        "Test Business",
                        "REG123",
                        BusinessType.values()[0],
                        "Technology",
                        "Test Address",
                        "Hyderabad",
                        "Telangana",
                        "500001",
                        "India",
                        "test@example.com",
                        "9876543210",
                        new BigDecimal("100000"),
                        20,
                        LocalDate.of(2020, 1, 1)
                );

        assertEquals("Test Business", request.businessName());
        assertEquals("REG123", request.registrationNumber());
        assertEquals(BusinessType.values()[0], request.businessType());
        assertEquals("Technology", request.industry());
        assertEquals("Test Address", request.address());
        assertEquals("Hyderabad", request.city());
        assertEquals("Telangana", request.state());
        assertEquals("500001", request.postalCode());
        assertEquals("India", request.country());
        assertEquals("test@example.com", request.contactEmail());
        assertEquals("9876543210", request.contactPhone());
        assertEquals(new BigDecimal("100000"), request.annualRevenue());
        assertEquals(20, request.employeeCount());
        assertEquals(LocalDate.of(2020, 1, 1), request.establishedDate());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {
        CreateBusinessProfileRequest first =
                new CreateBusinessProfileRequest(
                        "Test", "REG123", BusinessType.values()[0],
                        "IT", "Address", "City", "State",
                        "500001", "India", "test@example.com",
                        "9876543210", BigDecimal.TEN, 10,
                        LocalDate.of(2020, 1, 1)
                );

        CreateBusinessProfileRequest second =
                new CreateBusinessProfileRequest(
                        "Test", "REG123", BusinessType.values()[0],
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