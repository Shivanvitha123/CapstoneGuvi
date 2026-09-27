package org.example.riskintelligenceservice.controller;

import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.dto.SimulationResponse;
import org.example.riskintelligenceservice.service.SimulationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationControllerTest {

    @Mock
    private SimulationService service;

    @InjectMocks
    private SimulationController controller;

    private Authentication auth(String id, String role) {
        return new UsernamePasswordAuthenticationToken(
                id, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Test
    void createSimulation_success() {
        SimulationRequest request = mock(SimulationRequest.class);
        SimulationResponse response = mock(SimulationResponse.class);

        when(service.createSimulation(request, 10L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.createSimulation(
                        request, auth("10", "BUSINESS_OWNER")))
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertSame(response, result.getBody());
                })
                .verifyComplete();

        verify(service).createSimulation(request, 10L, "BUSINESS_OWNER");
    }

    @Test
    void getSimulation_success() {
        SimulationResponse response = mock(SimulationResponse.class);

        when(service.getSimulation(1L, 10L, "ADMIN"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getSimulation(1L, auth("10", "ADMIN")))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getSimulations_success() {
        SimulationResponse response = mock(SimulationResponse.class);

        when(service.getSimulations(10L, "ADMIN"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getSimulations(auth("10", "ADMIN")))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getSimulations_emptyResult() {
        when(service.getSimulations(10L, "BUSINESS_OWNER"))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        controller.getSimulations(auth("10", "BUSINESS_OWNER")))
                .verifyComplete();
    }

    @Test
    void getSimulation_serviceErrorPropagates() {
        when(service.getSimulation(99L, 10L, "ADMIN"))
                .thenReturn(Mono.error(
                        new RuntimeException("Simulation not found")));

        StepVerifier.create(
                        controller.getSimulation(99L, auth("10", "ADMIN")))
                .expectErrorMessage("Simulation not found")
                .verify();
    }
}