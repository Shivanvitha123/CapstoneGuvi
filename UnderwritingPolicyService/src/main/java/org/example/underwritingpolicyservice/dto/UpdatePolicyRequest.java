package org.example.underwritingpolicyservice.dto;




import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePolicyRequest(

        @Positive(message = "Business ID must be positive")
        Long businessId,

        String policyNumber,

        String policyType,

        @DecimalMin(
                value = "0.01",
                message = "Coverage amount must be greater than zero"
        )
        BigDecimal coverageAmount,

        @DecimalMin(
                value = "0.01",
                message = "Premium amount must be greater than zero"
        )
        BigDecimal premiumAmount,

        LocalDate startDate,

        LocalDate endDate,

        String description
) {
}

