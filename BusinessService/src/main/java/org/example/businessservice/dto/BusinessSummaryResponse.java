package org.example.businessservice.dto;


import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;

public record BusinessSummaryResponse(

        Long id,

        Long ownerId,

        String businessName,

        String registrationNumber,

        BusinessType businessType,

        String industry,

        BusinessStatus status
) {
}

