package org.example.underwritingpolicyservice.service;

import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.model.PolicyStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PolicyService {

    Mono<PolicyResponse> createPolicy(
            CreatePolicyRequest request,
            Long userId,
            String role
    );

    Mono<PolicyResponse> getPolicy(
            Long policyId,
            Long userId,
            String role
    );

    Flux<PolicyResponse> getOwnerPolicies(
            Long userId,
            String role
    );

    Flux<PolicyResponse> getAllPolicies(
            Long userId,
            String role
    );

    Mono<PolicyResponse> updatePolicy(
            Long policyId,
            UpdatePolicyRequest request,
            Long userId,
            String role
    );

    Mono<PolicyResponse> submitPolicy(
            Long policyId,
            Long userId,
            String role
    );

    Mono<PolicyResponse> updatePolicyStatus(
            Long policyId,
            PolicyStatus status,
            Long userId,
            String role
    );
}
