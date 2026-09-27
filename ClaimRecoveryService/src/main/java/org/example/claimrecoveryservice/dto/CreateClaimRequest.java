package org.example.claimrecoveryservice.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.claimrecoveryservice.model.ClaimType;


import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateClaimRequest(

        @NotNull(message = "Business ID is required")
        Long businessId,

        @NotNull(message = "Policy ID is required")
        Long policyId,

        @NotBlank(message = "Claim number is required")
        String claimNumber,

        @NotNull(message = "Claim type is required")
        ClaimType claimType,

        @NotNull(message = "Incident date is required")
        LocalDate incidentDate,

        @NotNull(message = "Reported date is required")
        LocalDate reportedDate,

        @NotNull(message = "Claimed amount is required")
        @DecimalMin(value = "0.01")
        BigDecimal claimedAmount,

        String description
) {
}

