package org.example.claimrecoveryservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRecoveryRequest(

        @NotNull(message = "Recovery amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Recovery amount must be greater than 0"
        )
        BigDecimal recoveryAmount,

        String recoverySource,

        String description

) {
}
