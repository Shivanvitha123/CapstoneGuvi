package org.example.underwritingpolicyservice.exception;

public class PolicyAccessDeniedException extends RuntimeException {
    public PolicyAccessDeniedException(String message) {
        super(message);
    }
}
