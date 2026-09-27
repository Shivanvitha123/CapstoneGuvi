package org.example.businessservice.dto;

import org.example.businessservice.model.DeletionRequestStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessDeletionRequestResponseTest {

    @Test
    void shouldCreateAndReadDeletionResponse() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime reviewed = created.plusHours(2);

        BusinessDeletionRequestResponse response =
                new BusinessDeletionRequestResponse(
                        1L,
                        2L,
                        10L,
                        20L,
                        "Business closed",
                        DeletionRequestStatus.values()[0],
                        created,
                        reviewed
                );

        assertEquals(1L, response.id());
        assertEquals(2L, response.businessId());
        assertEquals(10L, response.ownerId());
        assertEquals(20L, response.reviewerId());
        assertEquals("Business closed", response.reason());
        assertEquals(DeletionRequestStatus.values()[0], response.status());
        assertEquals(created, response.createdAt());
        assertEquals(reviewed, response.reviewedAt());
    }

    @Test
    void shouldSupportEqualsHashCodeAndToString() {
        LocalDateTime now = LocalDateTime.now();

        BusinessDeletionRequestResponse first =
                new BusinessDeletionRequestResponse(
                        1L, 2L, 10L, 20L, "Reason",
                        DeletionRequestStatus.values()[0], now, now
                );

        BusinessDeletionRequestResponse second =
                new BusinessDeletionRequestResponse(
                        1L, 2L, 10L, 20L, "Reason",
                        DeletionRequestStatus.values()[0], now, now
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertTrue(first.toString().contains("Reason"));
    }
}