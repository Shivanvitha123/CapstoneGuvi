package org.example.underwritingpolicyservice.exception;

public class PolicyAlreadyExistsException extends RuntimeException {
    public PolicyAlreadyExistsException(String message) {
        super(message);
    }
}
