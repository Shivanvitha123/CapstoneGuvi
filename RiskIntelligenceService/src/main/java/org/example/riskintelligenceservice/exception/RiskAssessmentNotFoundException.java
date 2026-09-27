package org.example.riskintelligenceservice.exception;


public class RiskAssessmentNotFoundException
        extends RuntimeException {

    public RiskAssessmentNotFoundException(Long id) {

        super("Risk assessment not found with id: " + id);
    }
}

