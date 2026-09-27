package org.example.businessservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEnumsTest {

    @Test
    void businessStatusContainsExpectedValues() {
        assertArrayEquals(
                new BusinessStatus[]{
                        BusinessStatus.ACTIVE,
                        BusinessStatus.INACTIVE,
                        BusinessStatus.PENDING
                },
                BusinessStatus.values()
        );

        assertEquals(
                BusinessStatus.ACTIVE,
                BusinessStatus.valueOf("ACTIVE")
        );
    }

    @Test
    void businessTypeContainsExpectedValues() {
        assertArrayEquals(
                new BusinessType[]{
                        BusinessType.SOLE_PROPRIETORSHIP,
                        BusinessType.PARTNERSHIP,
                        BusinessType.LLP,
                        BusinessType.PRIVATE_LIMITED,
                        BusinessType.PUBLIC_LIMITED,
                        BusinessType.OTHER
                },
                BusinessType.values()
        );

        assertEquals(
                BusinessType.LLP,
                BusinessType.valueOf("LLP")
        );
    }

    @Test
    void deletionRequestStatusContainsExpectedValues() {
        assertArrayEquals(
                new DeletionRequestStatus[]{
                        DeletionRequestStatus.PENDING,
                        DeletionRequestStatus.APPROVED,
                        DeletionRequestStatus.REJECTED
                },
                DeletionRequestStatus.values()
        );

        assertEquals(
                DeletionRequestStatus.PENDING,
                DeletionRequestStatus.valueOf("PENDING")
        );
    }
}