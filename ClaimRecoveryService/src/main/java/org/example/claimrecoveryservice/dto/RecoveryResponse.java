package org.example.claimrecoveryservice.dto;



import org.example.claimrecoveryservice.model.RecoveryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecoveryResponse(

        Long id,

        Long claimId,

        Long ownerId,

        BigDecimal recoveryAmount,

        String recoverySource,

        String description,

        RecoveryStatus status,

        Long processedBy,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}

