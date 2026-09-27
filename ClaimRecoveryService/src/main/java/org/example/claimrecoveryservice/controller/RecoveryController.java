package org.example.claimrecoveryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.dto.RecoveryResponse;
import org.example.claimrecoveryservice.dto.UpdateRecoveryStatusRequest;
import org.example.claimrecoveryservice.service.RecoveryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class RecoveryController {

    private final RecoveryService service;

    @PostMapping("/{claimId}/recovery")
    public Mono<ResponseEntity<RecoveryResponse>> createRecovery(
            @PathVariable Long claimId,
            @Valid @RequestBody CreateRecoveryRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.createRecovery(
                        claimId,
                        request,
                        userId,
                        role
                )
                .map(response ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response)
                );
    }

    @GetMapping("/{claimId}/recovery")
    public Flux<RecoveryResponse> getClaimRecoveries(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaimRecoveries(
                claimId,
                userId,
                role
        );
    }

    @GetMapping("/recovery")
    public Flux<RecoveryResponse> getAllRecoveries(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getAllRecoveries(userId, role);
    }

    @PatchMapping("/recovery/{recoveryId}/status")
    public Mono<RecoveryResponse> updateRecoveryStatus(
            @PathVariable Long recoveryId,
            @Valid @RequestBody UpdateRecoveryStatusRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.updateRecoveryStatus(
                recoveryId,
                request,
                userId,
                role
        );
    }

    private Long getUserId(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (Exception exception) {
            return null;
        }
    }

    private String getRole(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        return authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority().replace("ROLE_", "")
                )
                .orElse(null);
    }
}