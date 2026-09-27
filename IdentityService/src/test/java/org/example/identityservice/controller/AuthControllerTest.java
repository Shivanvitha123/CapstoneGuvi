package org.example.identityservice.controller;

import org.example.identityservice.dto.AuthResponse;
import org.example.identityservice.dto.LoginRequest;
import org.example.identityservice.dto.RegisterRequest;
import org.example.identityservice.dto.UserResponse;
import org.example.identityservice.model.Role;
import org.example.identityservice.service.AuthService;
import org.example.identityservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private UserService userService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userService = mock(UserService.class);
        controller = new AuthController(authService, userService);
    }

    private AuthResponse authResponse() {
        return new AuthResponse(
                "test-token",
                1L,
                "Test User",
                "test@example.com",
                Role.BUSINESS_OWNER
        );
    }

    private UserResponse userResponse(Long id) {
        LocalDateTime now = LocalDateTime.now();

        return new UserResponse(
                id,
                "Test User",
                "test@example.com",
                Role.BUSINESS_OWNER,
                true,
                now,
                now
        );
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest request =
                new RegisterRequest("Test User", "test@example.com", "password123");

        when(authService.register(request))
                .thenReturn(Mono.just(authResponse()));

        StepVerifier.create(controller.register(request))
                .assertNext(response -> {
                    assertEquals("test-token", response.token());
                    assertEquals(1L, response.userId());
                    assertEquals("Test User", response.name());
                })
                .verifyComplete();

        verify(authService).register(request);
    }

    @Test
    void shouldLoginUser() {
        LoginRequest request =
                new LoginRequest("test@example.com", "password123");

        when(authService.login(request))
                .thenReturn(Mono.just(authResponse()));

        StepVerifier.create(controller.login(request))
                .assertNext(response -> {
                    assertEquals("test-token", response.token());
                    assertEquals(Role.BUSINESS_OWNER, response.role());
                })
                .verifyComplete();

        verify(authService).login(request);
    }

    @Test
    void shouldGetCurrentUser() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("1", null);

        UserResponse expected = userResponse(1L);

        when(userService.getUserById(1L))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(controller.getCurrentUser(authentication))
                .assertNext(response -> assertEquals(1L, response.id()))
                .verifyComplete();

        verify(userService).getUserById(1L);
    }

    @Test
    void shouldGetAllUsers() {
        UserResponse first = userResponse(1L);
        UserResponse second = userResponse(2L);

        when(userService.getAllUsers())
                .thenReturn(Flux.just(first, second));

        StepVerifier.create(controller.getAllUsers())
                .expectNext(first)
                .expectNext(second)
                .verifyComplete();

        verify(userService).getAllUsers();
    }

    @Test
    void shouldReturnEmptyWhenThereAreNoUsers() {
        when(userService.getAllUsers())
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllUsers())
                .verifyComplete();

        verify(userService).getAllUsers();
    }

    @Test
    void shouldGetUserById() {
        UserResponse expected = userResponse(10L);

        when(userService.getUserById(10L))
                .thenReturn(Mono.just(expected));

        StepVerifier.create(controller.getUserById(10L))
                .assertNext(response -> assertEquals(10L, response.id()))
                .verifyComplete();

        verify(userService).getUserById(10L);
    }

    @Test
    void shouldPropagateRegistrationError() {
        RegisterRequest request =
                new RegisterRequest("Test User", "test@example.com", "password123");

        when(authService.register(request))
                .thenReturn(Mono.error(
                        new RuntimeException("Registration failed")
                ));

        StepVerifier.create(controller.register(request))
                .expectErrorMessage("Registration failed")
                .verify();
    }

    @Test
    void shouldPropagateLoginError() {
        LoginRequest request =
                new LoginRequest("test@example.com", "password123");

        when(authService.login(request))
                .thenReturn(Mono.error(
                        new RuntimeException("Login failed")
                ));

        StepVerifier.create(controller.login(request))
                .expectErrorMessage("Login failed")
                .verify();
    }
}