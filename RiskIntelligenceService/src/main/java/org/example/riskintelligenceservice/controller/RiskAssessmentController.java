package org.example.riskintelligenceservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import org.example.riskintelligenceservice.service.RiskAssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/risk/assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService service;

    @PostMapping
    public Mono<ResponseEntity<RiskAssessmentResponse>> createAssessment(
            @Valid @RequestBody RiskAssessmentRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.createAssessment(request, userId, role)
                .map(response ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(response)
                );
    }

    /**
     * Retrieve assessments visible to the authenticated user.
     * Staff can see all assessments; owners see their own.
     */
    @GetMapping
    public Flux<RiskAssessmentResponse> getAllAssessments(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getAllAssessments(userId, role);
    }

    @GetMapping("/{id}")
    public Mono<RiskAssessmentResponse> getAssessment(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getAssessment(id, userId, role);
    }

    @GetMapping("/business/{businessId}")
    public Flux<RiskAssessmentResponse> getByBusiness(
            @PathVariable Long businessId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getByBusiness(businessId, userId, role);
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user ID must be numeric"
            );
        }
    }

    private String getRole(Authentication authentication) {
        if (authentication == null
                || authentication.getAuthorities() == null
                || authentication.getAuthorities().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Authenticated user role is missing"
            );
        }

        return authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replaceFirst("^ROLE_", "")
                .trim()
                .toUpperCase();
    }
}