package org.example.businessservice.dto;

import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessProfileResponseTest {

    @Test
    void shouldCreateAndReadBusinessProfileResponse() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate established = LocalDate.of(2020, 1, 1);

        BusinessProfileResponse response = new BusinessProfileResponse(
                1L,
                10L,
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
                new BigDecimal("1000000"),
                50,
                established,
                BusinessStatus.values()[0],
                now,
                now
        );

        assertEquals(1L, response.id());
        assertEquals(10L, response.ownerId());
        assertEquals("Test Business", response.businessName());
        assertEquals("REG123", response.registrationNumber());
        assertEquals(BusinessType.values()[0], response.businessType());
        assertEquals("Technology", response.industry());
        assertEquals("Test Address", response.address());
        assertEquals("Hyderabad", response.city());
        assertEquals("Telangana", response.state());
        assertEquals("500001", response.postalCode());
        assertEquals("India", response.country());
        assertEquals("test@example.com", response.contactEmail());
        assertEquals("9876543210", response.contactPhone());
        assertEquals(new BigDecimal("1000000"), response.annualRevenue());
        assertEquals(50, response.employeeCount());
        assertEquals(established, response.establishedDate());
        assertEquals(BusinessStatus.values()[0], response.status());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {
        BusinessProfileResponse first = new BusinessProfileResponse(
                1L, 10L, "Test", "REG123",
                BusinessType.values()[0], "IT", "Address",
                "Hyderabad", "Telangana", "500001", "India",
                "test@example.com", "9876543210",
                BigDecimal.TEN, 10, LocalDate.of(2020, 1, 1),
                BusinessStatus.values()[0],
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        BusinessProfileResponse second = new BusinessProfileResponse(
                1L, 10L, "Test", "REG123",
                BusinessType.values()[0], "IT", "Address",
                "Hyderabad", "Telangana", "500001", "India",
                "test@example.com", "9876543210",
                BigDecimal.TEN, 10, LocalDate.of(2020, 1, 1),
                BusinessStatus.values()[0],
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 1, 10, 0)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("Test"));
    }
}
