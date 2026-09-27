package org.example.identityservice.loader;

import org.example.identityservice.config.SeedUserProperties;
import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PreloadedUserLoaderTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private SeedUserProperties properties;
    private PreloadedUserLoader loader;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        properties = new SeedUserProperties();

        loader = new PreloadedUserLoader(
                userRepository,
                passwordEncoder,
                properties
        );
    }

    private SeedUserProperties.SeedUser createSeedUser(
            String name,
            String email,
            String password,
            Role role
    ) {
        SeedUserProperties.SeedUser seed =
                new SeedUserProperties.SeedUser();

        seed.setName(name);
        seed.setEmail(email);
        seed.setPassword(password);
        seed.setRole(role);

        return seed;
    }

    @Test
    void shouldCreateUserWhenUserDoesNotExist() {
        SeedUserProperties.SeedUser seed = createSeedUser(
                "Admin User",
                "ADMIN@EXAMPLE.COM",
                "admin12345",
                Role.ADMIN
        );

        properties.setPreloadedUsers(List.of(seed));

        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Mono.empty());

        when(passwordEncoder.encode("admin12345"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        loader.run();

        verify(userRepository).findByEmail("admin@example.com");
        verify(passwordEncoder).encode("admin12345");

        verify(userRepository).save(argThat(user ->
                "Admin User".equals(user.getName())
                        && "admin@example.com".equals(user.getEmail())
                        && "encoded-password".equals(user.getPassword())
                        && user.getRole() == Role.ADMIN
                        && Boolean.TRUE.equals(user.getActive())
                        && user.getCreatedAt() != null
                        && user.getUpdatedAt() != null
        ));
    }

    @Test
    void shouldNotCreateUserWhenUserAlreadyExists() {
        SeedUserProperties.SeedUser seed = createSeedUser(
                "Existing User",
                "existing@example.com",
                "password123",
                Role.BUSINESS_OWNER
        );

        properties.setPreloadedUsers(List.of(seed));

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .email("existing@example.com")
                        .build()));

        loader.run();

        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldHandleEmptyPreloadedUsers() {
        properties.setPreloadedUsers(List.of());

        loader.run();

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldCreateMultipleMissingUsers() {
        SeedUserProperties.SeedUser admin = createSeedUser(
                "Admin",
                "admin@example.com",
                "admin12345",
                Role.ADMIN
        );

        SeedUserProperties.SeedUser owner = createSeedUser(
                "Owner",
                "owner@example.com",
                "owner12345",
                Role.BUSINESS_OWNER
        );

        properties.setPreloadedUsers(List.of(admin, owner));

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        loader.run();

        verify(userRepository).findByEmail("admin@example.com");
        verify(userRepository).findByEmail("owner@example.com");
        verify(userRepository, times(2)).save(any(User.class));
        verify(passwordEncoder, times(2)).encode(anyString());
    }

    @Test
    void shouldNormalizeEmailBeforeCheckingRepository() {
        SeedUserProperties.SeedUser seed = createSeedUser(
                "Test User",
                "  TEST@EXAMPLE.COM ",
                "password123",
                Role.ADMIN
        );

        properties.setPreloadedUsers(List.of(seed));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        loader.run();

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(argThat(user ->
                "test@example.com".equals(user.getEmail())
        ));
    }
}