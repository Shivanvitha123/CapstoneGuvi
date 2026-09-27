package org.example.riskintelligenceservice.service;

import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.entity.Simulation;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.exception.SimulationNotFoundException;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.model.SimulationStatus;
import org.example.riskintelligenceservice.repository.SimulationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceImplTest {

    @Mock
    private SimulationRepository repository;

    @InjectMocks
    private SimulationServiceImpl service;

    private SimulationRequest request(
            String scenarioName, String scenarioInput) {
        SimulationRequest request = mock(SimulationRequest.class);
        when(request.businessId()).thenReturn(100L);
        when(request.policyId()).thenReturn(200L);
        when(request.scenarioName()).thenReturn(scenarioName);
        when(request.scenarioInput()).thenReturn(scenarioInput);
        return request;
    }

    private Simulation simulation(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();

        return Simulation.builder()
                .id(1L)
                .businessId(100L)
                .policyId(200L)
                .ownerId(ownerId)
                .scenarioName("Flood")
                .scenarioInput("Heavy rainfall")
                .projectedRiskScore(new BigDecimal("60.00"))
                .projectedRiskLevel(RiskLevel.HIGH)
                .resultSummary("Simulation completed")
                .status(SimulationStatus.COMPLETED)
                .createdBy(ownerId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createSimulation_success() {
        when(repository.save(any(Simulation.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createSimulation(
                        request("Flood", "Heavy rainfall"),
                        10L, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(100L, response.businessId());
                    assertEquals(200L, response.policyId());
                    assertEquals("Flood", response.scenarioName());
                    assertEquals("Heavy rainfall",
                            response.scenarioInput());
                    assertNotNull(response.projectedRiskScore());
                    assertTrue(response.projectedRiskScore()
                            .compareTo(BigDecimal.ZERO) >= 0);
                    assertTrue(response.projectedRiskScore()
                            .compareTo(BigDecimal.valueOf(100)) <= 0);
                    assertNotNull(response.projectedRiskLevel());
                    assertEquals(SimulationStatus.COMPLETED,
                            response.status());
                    assertEquals(10L, response.createdBy());
                    assertNotNull(response.createdAt());
                    assertNotNull(response.updatedAt());
                    assertTrue(response.resultSummary()
                            .contains("Flood"));
                })
                .verifyComplete();

        verify(repository).save(any(Simulation.class));
    }

    @Test
    void createSimulation_sameInputProducesSameScore() {
        when(repository.save(any(Simulation.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createSimulation(
                        request("Flood", "Heavy rainfall"),
                        10L, "BUSINESS_OWNER"))
                .assertNext(first -> {
                    StepVerifier.create(service.createSimulation(
                                    request("Flood", "Heavy rainfall"),
                                    10L, "BUSINESS_OWNER"))
                            .assertNext(second ->
                                    assertEquals(
                                            first.projectedRiskScore(),
                                            second.projectedRiskScore()))
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    void createSimulation_differentInputProducesValidScore() {
        when(repository.save(any(Simulation.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.createSimulation(
                        request("Earthquake", "Magnitude 6"),
                        10L, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertNotNull(response.projectedRiskScore());
                    assertTrue(response.projectedRiskScore()
                            .compareTo(BigDecimal.ZERO) >= 0);
                    assertTrue(response.projectedRiskScore()
                            .compareTo(BigDecimal.valueOf(100)) <= 0);
                })
                .verifyComplete();
    }

    @Test
    void getSimulation_ownerCanViewOwnSimulation() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation(10L)));

        StepVerifier.create(service.getSimulation(
                        1L, 10L, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals("Flood", response.scenarioName());
                    assertEquals(SimulationStatus.COMPLETED,
                            response.status());
                })
                .verifyComplete();
    }

    @Test
    void getSimulation_adminCanViewAnySimulation() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation(10L)));

        StepVerifier.create(service.getSimulation(
                        1L, 99L, "ADMIN"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getSimulation_underwriterCanViewAnySimulation() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation(10L)));

        StepVerifier.create(service.getSimulation(
                        1L, 99L, "UNDERWRITER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getSimulation_riskEngineerCanViewAnySimulation() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation(10L)));

        StepVerifier.create(service.getSimulation(
                        1L, 99L, "RISK_ENGINEER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getSimulation_ownerCannotViewOthersSimulation() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation(10L)));

        StepVerifier.create(service.getSimulation(
                        1L, 99L, "BUSINESS_OWNER"))
                .expectError(RiskAccessDeniedException.class)
                .verify();
    }

    @Test
    void getSimulation_notFound() {
        when(repository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getSimulation(
                        99L, 10L, "ADMIN"))
                .expectError(SimulationNotFoundException.class)
                .verify();
    }

    @Test
    void getSimulations_staffGetsAll() {
        when(repository.findAll())
                .thenReturn(Flux.just(simulation(10L)));

        StepVerifier.create(service.getSimulations(
                        99L, "ADMIN"))
                .assertNext(response ->
                        assertEquals(1L, response.id()))
                .verifyComplete();

        verify(repository).findAll();
        verify(repository, never()).findByOwnerId(any());
    }

    @Test
    void getSimulations_underwriterGetsAll() {
        when(repository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getSimulations(
                        99L, "UNDERWRITER"))
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getSimulations_riskEngineerGetsAll() {
        when(repository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getSimulations(
                        99L, "RISK_ENGINEER"))
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getSimulations_businessOwnerGetsOwn() {
        when(repository.findByOwnerId(10L))
                .thenReturn(Flux.just(simulation(10L)));

        StepVerifier.create(service.getSimulations(
                        10L, "BUSINESS_OWNER"))
                .assertNext(response ->
                        assertEquals(10L, response.createdBy()))
                .verifyComplete();

        verify(repository).findByOwnerId(10L);
        verify(repository, never()).findAll();
    }

    @Test
    void getSimulations_unknownRoleGetsOwn() {
        when(repository.findByOwnerId(10L))
                .thenReturn(Flux.empty());

        StepVerifier.create(service.getSimulations(
                        10L, "UNKNOWN"))
                .verifyComplete();

        verify(repository).findByOwnerId(10L);
    }
}