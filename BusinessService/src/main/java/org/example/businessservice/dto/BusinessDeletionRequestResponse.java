package org.example.businessservice.dto;

import org.example.businessservice.model.DeletionRequestStatus;
import java.time.LocalDateTime;

public record BusinessDeletionRequestResponse(
        Long id,
        Long businessId,
        Long ownerId,
        Long reviewerId,
        String reason,
        DeletionRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {
}