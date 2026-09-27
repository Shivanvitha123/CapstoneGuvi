package org.example.underwritingpolicyservice.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePolicyRequest(

        @NotNull(message = "Business ID is required")
        @Positive(message = "Business ID must be positive")
        Long businessId,

        @NotBlank(message = "Policy number is required")
        String policyNumber,

        @NotBlank(message = "Policy type is required")
        String policyType,

        @NotNull(message = "Coverage amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Coverage amount must be greater than zero"
        )
        BigDecimal coverageAmount,

        @NotNull(message = "Premium amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Premium amount must be greater than zero"
        )
        BigDecimal premiumAmount,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(
                message = "Start date cannot be in the past"
        )
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        String description
) {
}

