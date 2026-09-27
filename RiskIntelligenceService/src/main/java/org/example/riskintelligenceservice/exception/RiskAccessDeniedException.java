package org.example.riskintelligenceservice.exception;


public class RiskAccessDeniedException
        extends RuntimeException {

    public RiskAccessDeniedException() {

        super("You are not authorized to access this risk resource");
    }
}


