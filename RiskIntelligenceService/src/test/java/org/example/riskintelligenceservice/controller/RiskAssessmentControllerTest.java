package org.example.riskintelligenceservice.controller;

import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import org.example.riskintelligenceservice.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentControllerTest {

    @Mock
    private RiskAssessmentService service;

    @InjectMocks
    private RiskAssessmentController controller;

    private Authentication auth(String id, String role) {
        return new UsernamePasswordAuthenticationToken(
                id, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Test
    void createAssessment_success() {
        RiskAssessmentRequest request = mock(RiskAssessmentRequest.class);
        RiskAssessmentResponse response = mock(RiskAssessmentResponse.class);

        when(service.createAssessment(request, 10L, "RISK_ENGINEER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.createAssessment(
                        request, auth("10", "RISK_ENGINEER")))
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertSame(response, result.getBody());
                })
                .verifyComplete();

        verify(service).createAssessment(request, 10L, "RISK_ENGINEER");
    }

    @Test
    void getAllAssessments_success() {
        RiskAssessmentResponse response = mock(RiskAssessmentResponse.class);

        when(service.getAllAssessments(10L, "ADMIN"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getAllAssessments(auth("10", "ADMIN")))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getAssessment_success() {
        RiskAssessmentResponse response = mock(RiskAssessmentResponse.class);

        when(service.getAssessment(1L, 10L, "ADMIN"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getAssessment(1L, auth("10", "ADMIN")))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getByBusiness_success() {
        RiskAssessmentResponse response = mock(RiskAssessmentResponse.class);

        when(service.getByBusiness(100L, 10L, "ADMIN"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getByBusiness(
                        100L, auth("10", "ADMIN")))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getUserId_nullAuthentication_unauthorized() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getAllAssessments(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getUserId_blankName_unauthorized() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getAllAssessments(authentication)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getUserId_nonNumericName_unauthorized() {
        Authentication authentication = auth("abc", "ADMIN");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getAllAssessments(authentication)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getRole_missingAuthorities_forbidden() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "10", null, List.of()
                );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getAllAssessments(authentication)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void getRole_removesPrefixAndUppercases() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "10", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        when(service.getAllAssessments(10L, "ADMIN"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllAssessments(authentication))
                .verifyComplete();

        verify(service).getAllAssessments(10L, "ADMIN");
    }
}