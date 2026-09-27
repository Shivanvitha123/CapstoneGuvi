package org.example.claimrecoveryservice.dto;


import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClaimResponse(

        Long id,

        Long businessId,

        Long policyId,

        Long ownerId,

        String claimNumber,

        ClaimType claimType,

        LocalDate incidentDate,

        LocalDate reportedDate,

        BigDecimal claimedAmount,

        BigDecimal approvedAmount,

        String description,

        ClaimStatus status,

        Long assignedAdjusterId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}

