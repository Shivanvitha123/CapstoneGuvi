package org.example.businessservice.entity;

import java.time.LocalDateTime;

import org.example.businessservice.model.DeletionRequestStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("business_deletion_requests")
public class BusinessDeletionRequest {

    @Id
    private Long id;

    @Column("business_id")
    private Long businessId;

    @Column("owner_id")
    private Long ownerId;

    @Column("reviewer_id")
    private Long reviewerId;

    private String reason;

    private DeletionRequestStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("reviewed_at")
    private LocalDateTime reviewedAt;
}