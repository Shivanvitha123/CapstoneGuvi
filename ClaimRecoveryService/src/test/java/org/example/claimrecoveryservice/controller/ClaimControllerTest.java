package org.example.claimrecoveryservice.controller;

import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;
import org.example.claimrecoveryservice.service.ClaimFileService;
import org.example.claimrecoveryservice.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    @Mock
    private ClaimService service;

    @Mock
    private ClaimFileService claimFileService;

    @InjectMocks
    private ClaimController controller;

    private UsernamePasswordAuthenticationToken ownerAuth;
    private ClaimResponse response;

    @BeforeEach
    void setUp() {
        ownerAuth = new UsernamePasswordAuthenticationToken(
                "100",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_BUSINESS_OWNER"))
        );

        response = new ClaimResponse(
                1L,
                10L,
                20L,
                100L,
                "CLM-001",
                ClaimType.PROPERTY_DAMAGE,
                LocalDate.now(),
                LocalDate.now(),
                new BigDecimal("5000"),
                new BigDecimal("4000"),
                "Property damage",
                ClaimStatus.SUBMITTED,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createClaimReturnsCreated() {
        CreateClaimRequest request = mock(CreateClaimRequest.class);

        when(service.createClaim(request, 100L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.createClaim(request, ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();

        verify(service).createClaim(request, 100L, "BUSINESS_OWNER");
    }

    @Test
    void getAllClaimsReturnsOk() {
        when(service.getAllClaims(100L, "BUSINESS_OWNER"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getAllClaims(ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());

                    StepVerifier.create(result.getBody())
                            .expectNext(response)
                            .verifyComplete();
                })
                .verifyComplete();

        verify(service).getAllClaims(100L, "BUSINESS_OWNER");
    }

    @Test
    void getOwnerClaimsReturnsOk() {
        when(service.getOwnerClaims(100L, "BUSINESS_OWNER"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getOwnerClaims(ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());

                    StepVerifier.create(result.getBody())
                            .expectNext(response)
                            .verifyComplete();
                })
                .verifyComplete();

        verify(service).getOwnerClaims(100L, "BUSINESS_OWNER");
    }

    @Test
    void getClaimReturnsClaim() {
        when(service.getClaim(1L, 100L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getClaim(1L, ownerAuth))
                .expectNext(response)
                .verifyComplete();

        verify(service).getClaim(1L, 100L, "BUSINESS_OWNER");
    }

    @Test
    void updateClaimStatusReturnsClaim() {
        UpdateClaimStatusRequest request =
                mock(UpdateClaimStatusRequest.class);

        when(service.updateClaimStatus(
                1L, request, 100L, "BUSINESS_OWNER"
        )).thenReturn(Mono.just(response));

        StepVerifier.create(
                        controller.updateClaimStatus(1L, request, ownerAuth)
                )
                .expectNext(response)
                .verifyComplete();

        verify(service).updateClaimStatus(
                1L, request, 100L, "BUSINESS_OWNER"
        );
    }

    @Test
    void uploadClaimDocumentReturnsCreated() {
        FilePart file = mock(FilePart.class);

        when(service.getClaim(1L, 100L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        when(claimFileService.uploadFile("1", file))
                .thenReturn(Mono.just("document.pdf"));

        StepVerifier.create(
                        controller.uploadClaimDocument(1L, file, ownerAuth)
                )
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertEquals("document.pdf", result.getBody());
                })
                .verifyComplete();

        verify(service).getClaim(1L, 100L, "BUSINESS_OWNER");
        verify(claimFileService).uploadFile("1", file);
    }

    @Test
    void getClaimDocumentsReturnsFiles() {
        List<String> files = List.of("document.pdf", "image.png");

        when(service.getClaim(1L, 100L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        when(claimFileService.listFiles("1"))
                .thenReturn(Mono.just(files));

        StepVerifier.create(
                        controller.getClaimDocuments(1L, ownerAuth)
                )
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertEquals(files, result.getBody());
                })
                .verifyComplete();

        verify(claimFileService).listFiles("1");
    }

    @Test
    void downloadClaimDocumentReturnsResource() {
        Path path = Path.of("document.pdf");

        when(service.getClaim(1L, 100L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        when(claimFileService.getFile("1", "document.pdf"))
                .thenReturn(Mono.just(path));

        StepVerifier.create(
                        controller.downloadClaimDocument(
                                1L, "document.pdf", ownerAuth
                        )
                )
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertNotNull(result.getBody());
                    assertTrue(result.getBody() instanceof Resource);
                    assertEquals(
                            "attachment; filename=\"document.pdf\"",
                            result.getHeaders().getFirst("Content-Disposition")
                    );
                })
                .verifyComplete();

        verify(claimFileService).getFile("1", "document.pdf");
    }

    @Test
    void getUserIdReturnsNullForNullAuthentication() {
        when(service.getAllClaims(null, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllClaims(null))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    StepVerifier.create(result.getBody())
                            .verifyComplete();
                })
                .verifyComplete();

        verify(service).getAllClaims(null, null);
    }

    @Test
    void getUserIdReturnsNullForInvalidName() {
        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "invalid",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        when(service.getAllClaims(null, "ADMIN"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllClaims(authentication))
                .expectNextCount(1)
                .verifyComplete();

        verify(service).getAllClaims(null, "ADMIN");
    }

    @Test
    void getRoleReturnsNullForMissingAuthentication() {
        when(service.getOwnerClaims(null, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getOwnerClaims(null))
                .expectNextCount(1)
                .verifyComplete();

        verify(service).getOwnerClaims(null, null);
    }
}