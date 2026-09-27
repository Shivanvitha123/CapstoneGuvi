package org.example.identityservice.service;

import org.example.identityservice.dto.AuthResponse;
import org.example.identityservice.dto.LoginRequest;
import org.example.identityservice.dto.RegisterRequest;
import org.example.identityservice.entity.User;
import org.example.identityservice.exception.InvalidCredentialsException;
import org.example.identityservice.exception.UserAlreadyExistsException;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.example.identityservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .build();
    }

    @Test
    void shouldRegisterNewUser() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.name()).thenReturn(" Test User ");
        when(request.email()).thenReturn(" TEST@EXAMPLE.COM ");
        when(request.password()).thenReturn("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(Mono.just(false));

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("test-token");

        StepVerifier.create(authService.register(request))
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
                user.getName().equals("Test User")
                        && user.getEmail().equals("test@example.com")
                        && user.getPassword().equals("encoded-password")
                        && user.getRole() == Role.BUSINESS_OWNER
                        && Boolean.TRUE.equals(user.getActive())
                        && user.getCreatedAt() != null
                        && user.getUpdatedAt() != null
        ));
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.name()).thenReturn("Test User");
        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(authService.register(request))
                .expectError(UserAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = mock(LoginRequest.class);
        User user = createUser();

        when(request.email()).thenReturn(" TEST@EXAMPLE.COM ");
        when(request.password()).thenReturn("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("test-token");

        StepVerifier.create(authService.login(request))
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches(
                "password123",
                "encoded-password"
        );
        verify(jwtService).generateToken(user);
    }

    @Test
    void shouldRejectLoginForUnknownEmail() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.email()).thenReturn("unknown@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.login(request))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectLoginForInactiveUser() {
        LoginRequest request = mock(LoginRequest.class);
        User user = createUser();
        user.setActive(false);

        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(authService.login(request))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectLoginForIncorrectPassword() {
        LoginRequest request = mock(LoginRequest.class);
        User user = createUser();

        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("wrong-password");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        StepVerifier.create(authService.login(request))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectLoginWhenUserActiveIsNull() {
        LoginRequest request = mock(LoginRequest.class);
        User user = createUser();
        user.setActive(null);

        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(authService.login(request))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void shouldPropagateRepositoryErrorDuringRegistration() {
        RegisterRequest request = mock(RegisterRequest.class);

        when(request.name()).thenReturn("Test User");
        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(Mono.error(
                        new RuntimeException("Database error")
                ));

        StepVerifier.create(authService.register(request))
                .expectErrorMessage("Database error")
                .verify();
    }

    @Test
    void shouldPropagateRepositoryErrorDuringLogin() {
        LoginRequest request = mock(LoginRequest.class);

        when(request.email()).thenReturn("test@example.com");
        when(request.password()).thenReturn("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.error(
                        new RuntimeException("Database error")
                ));

        StepVerifier.create(authService.login(request))
                .expectErrorMessage("Database error")
                .verify();
    }
}