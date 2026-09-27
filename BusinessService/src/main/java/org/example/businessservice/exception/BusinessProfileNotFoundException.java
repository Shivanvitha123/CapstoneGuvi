package org.example.businessservice.exception;

public class BusinessProfileNotFoundException extends RuntimeException {
    public BusinessProfileNotFoundException(String message) {
        super(message);
    }
}
