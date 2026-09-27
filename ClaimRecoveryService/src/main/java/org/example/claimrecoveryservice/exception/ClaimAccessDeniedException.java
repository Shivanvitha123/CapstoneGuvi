package org.example.claimrecoveryservice.exception;


public class ClaimAccessDeniedException
        extends RuntimeException {

    public ClaimAccessDeniedException(
            String message) {

        super(message);
    }
}


