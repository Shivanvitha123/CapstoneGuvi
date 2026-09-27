package org.example.businessservice.exception;

public class BusinessProfileAlreadyExistsException extends RuntimeException {
    public BusinessProfileAlreadyExistsException(String message) {
        super(message);
    }
}
