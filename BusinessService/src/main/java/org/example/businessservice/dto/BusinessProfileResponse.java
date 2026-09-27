package org.example.businessservice.dto;

import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BusinessProfileResponse(

        Long id,

        Long ownerId,

        String businessName,

        String registrationNumber,

        BusinessType businessType,

        String industry,

        String address,

        String city,

        String state,

        String postalCode,

        String country,

        String contactEmail,

        String contactPhone,

        BigDecimal annualRevenue,

        Integer employeeCount,

        LocalDate establishedDate,

        BusinessStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}

