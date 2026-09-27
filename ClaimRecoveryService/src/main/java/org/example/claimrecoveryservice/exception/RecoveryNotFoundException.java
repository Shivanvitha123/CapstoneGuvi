package org.example.claimrecoveryservice.exception;


public class RecoveryNotFoundException
        extends RuntimeException {

    public RecoveryNotFoundException(Long id) {
        super("Recovery not found with id: " + id);
    }
}

