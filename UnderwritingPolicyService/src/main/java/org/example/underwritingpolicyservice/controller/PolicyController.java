package org.example.underwritingpolicyservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    public Mono<ResponseEntity<PolicyResponse>> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService
                .createPolicy(request, userId, role)
                .map(response ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response)
                );
    }

    @GetMapping("/owner")
    public Flux<PolicyResponse> getOwnerPolicies(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService.getOwnerPolicies(userId, role);
    }

    @GetMapping
    public Flux<PolicyResponse> getAllPolicies(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService.getAllPolicies(userId, role);
    }

    @GetMapping("/{policyId}")
    public Mono<ResponseEntity<PolicyResponse>> getPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService
                .getPolicy(policyId, userId, role)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{policyId}")
    public Mono<ResponseEntity<PolicyResponse>> updatePolicy(
            @PathVariable Long policyId,
            @Valid @RequestBody UpdatePolicyRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService
                .updatePolicy(policyId, request, userId, role)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{policyId}/submit")
    public Mono<ResponseEntity<PolicyResponse>> submitPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService
                .submitPolicy(policyId, userId, role)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{policyId}/status")
    public Mono<ResponseEntity<PolicyResponse>> updatePolicyStatus(
            @PathVariable Long policyId,
            @RequestParam PolicyStatus status,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return policyService
                .updatePolicyStatus(policyId, status, userId, role)
                .map(ResponseEntity::ok);
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null
                || authentication.getPrincipal() == null) {
            return null;
        }

        try {
            return Long.parseLong(
                    authentication.getPrincipal().toString()
            );
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String getRole(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        return authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority().replace("ROLE_", "")
                )
                .orElse(null);
    }
}