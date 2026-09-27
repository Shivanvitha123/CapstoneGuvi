package org.example.claimrecoveryservice.controller;

import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.dto.RecoveryResponse;
import org.example.claimrecoveryservice.dto.UpdateRecoveryStatusRequest;
import org.example.claimrecoveryservice.model.RecoveryStatus;
import org.example.claimrecoveryservice.service.RecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecoveryControllerTest {

    @Mock
    private RecoveryService service;

    @InjectMocks
    private RecoveryController controller;

    private UsernamePasswordAuthenticationToken adjusterAuth;
    private RecoveryResponse response;

    @BeforeEach
    void setUp() {
        adjusterAuth = new UsernamePasswordAuthenticationToken(
                "200",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CLAIMS_ADJUSTER"))
        );

        response = new RecoveryResponse(
                1L,
                10L,
                100L,
                new BigDecimal("2500"),
                "Insurance",
                "Recovery for claim",
                RecoveryStatus.INITIATED,
                200L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createRecoveryReturnsCreated() {
        CreateRecoveryRequest request = mock(CreateRecoveryRequest.class);

        when(service.createRecovery(
                10L, request, 200L, "CLAIMS_ADJUSTER"
        )).thenReturn(Mono.just(response));

        StepVerifier.create(
                        controller.createRecovery(10L, request, adjusterAuth)
                )
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();

        verify(service).createRecovery(
                10L, request, 200L, "CLAIMS_ADJUSTER"
        );
    }

    @Test
    void getClaimRecoveriesReturnsRecoveries() {
        when(service.getClaimRecoveries(
                10L, 200L, "CLAIMS_ADJUSTER"
        )).thenReturn(Flux.just(response));

        StepVerifier.create(
                        controller.getClaimRecoveries(10L, adjusterAuth)
                )
                .expectNext(response)
                .verifyComplete();

        verify(service).getClaimRecoveries(
                10L, 200L, "CLAIMS_ADJUSTER"
        );
    }

    @Test
    void getAllRecoveriesReturnsRecoveries() {
        when(service.getAllRecoveries(
                200L, "CLAIMS_ADJUSTER"
        )).thenReturn(Flux.just(response));

        StepVerifier.create(controller.getAllRecoveries(adjusterAuth))
                .expectNext(response)
                .verifyComplete();

        verify(service).getAllRecoveries(
                200L, "CLAIMS_ADJUSTER"
        );
    }

    @Test
    void updateRecoveryStatusReturnsUpdatedRecovery() {
        UpdateRecoveryStatusRequest request =
                mock(UpdateRecoveryStatusRequest.class);

        when(service.updateRecoveryStatus(
                1L, request, 200L, "CLAIMS_ADJUSTER"
        )).thenReturn(Mono.just(response));

        StepVerifier.create(
                        controller.updateRecoveryStatus(
                                1L, request, adjusterAuth
                        )
                )
                .expectNext(response)
                .verifyComplete();

        verify(service).updateRecoveryStatus(
                1L, request, 200L, "CLAIMS_ADJUSTER"
        );
    }

    @Test
    void getUserIdReturnsNullForNullAuthentication() {
        when(service.getAllRecoveries(null, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllRecoveries(null))
                .verifyComplete();

        verify(service).getAllRecoveries(null, null);
    }

    @Test
    void getUserIdReturnsNullForInvalidName() {
        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "invalid",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        when(service.getAllRecoveries(null, "ADMIN"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllRecoveries(authentication))
                .verifyComplete();

        verify(service).getAllRecoveries(null, "ADMIN");
    }

    @Test
    void getRoleReturnsNullForMissingAuthentication() {
        when(service.getAllRecoveries(null, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllRecoveries(null))
                .verifyComplete();

        verify(service).getAllRecoveries(null, null);
    }
}