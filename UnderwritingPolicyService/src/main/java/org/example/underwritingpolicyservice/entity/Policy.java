package org.example.underwritingpolicyservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("policies")
public class Policy {

    @Id
    private Long id;

    @Column("business_id")
    private Long businessId;

    @Column("owner_id")
    private Long ownerId;

    @Column("policy_number")
    private String policyNumber;

    @Column("policy_type")
    private String policyType;

    @Column("coverage_amount")
    private BigDecimal coverageAmount;

    @Column("premium_amount")
    private BigDecimal premiumAmount;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("status")
    private String status;

    @Column("description")
    private String description;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}

