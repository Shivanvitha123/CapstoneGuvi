package org.example.claimrecoveryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import org.example.claimrecoveryservice.service.ClaimFileService;
import org.example.claimrecoveryservice.service.ClaimService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService service;
    private final ClaimFileService claimFileService;

    // CREATE CLAIM
    @PostMapping
    public Mono<ResponseEntity<ClaimResponse>> createClaim(
            @Valid @RequestBody CreateClaimRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.createClaim(request, userId, role)
                .map(response -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response));
    }

    // GET ALL CLAIMS
    @GetMapping
    public Mono<ResponseEntity<Flux<ClaimResponse>>> getAllClaims(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return Mono.just(
                ResponseEntity.ok(service.getAllClaims(userId, role))
        );
    }

    // GET OWNER CLAIMS
    @GetMapping("/owner")
    public Mono<ResponseEntity<Flux<ClaimResponse>>> getOwnerClaims(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return Mono.just(
                ResponseEntity.ok(service.getOwnerClaims(userId, role))
        );
    }

    // GET CLAIM BY ID
    @GetMapping("/{claimId}")
    public Mono<ClaimResponse> getClaim(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaim(claimId, userId, role);
    }

    // UPDATE CLAIM STATUS
    @PatchMapping("/{claimId}/status")
    public Mono<ClaimResponse> updateClaimStatus(
            @PathVariable Long claimId,
            @Valid @RequestBody UpdateClaimStatusRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.updateClaimStatus(
                claimId,
                request,
                userId,
                role
        );
    }

    // UPLOAD CLAIM DOCUMENT
    @PostMapping(
            value = "/{claimId}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Mono<ResponseEntity<String>> uploadClaimDocument(
            @PathVariable Long claimId,
            @RequestPart("file") FilePart file,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaim(claimId, userId, role)
                .flatMap(claim ->
                        claimFileService.uploadFile(
                                claimId.toString(),
                                file
                        )
                )
                .map(filename ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(filename)
                );
    }

    // GET ALL DOCUMENTS FOR A CLAIM
    @GetMapping("/{claimId}/documents")
    public Mono<ResponseEntity<List<String>>> getClaimDocuments(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaim(claimId, userId, role)
                .then(
                        claimFileService.listFiles(
                                claimId.toString()
                        )
                )
                .map(ResponseEntity::ok);
    }

    // DOWNLOAD CLAIM DOCUMENT
    @GetMapping("/{claimId}/documents/{filename}")
    public Mono<ResponseEntity<Resource>> downloadClaimDocument(
            @PathVariable Long claimId,
            @PathVariable String filename,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaim(claimId, userId, role)
                .then(
                        claimFileService.getFile(
                                claimId.toString(),
                                filename
                        )
                )
                .map(path -> {
                    Resource resource = new FileSystemResource(path);

                    return ResponseEntity.ok()
                            .header(
                                    HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + filename + "\""
                            )
                            .contentType(
                                    MediaType.APPLICATION_OCTET_STREAM
                            )
                            .body(resource);
                });
    }

    // AUTHENTICATION HELPERS
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