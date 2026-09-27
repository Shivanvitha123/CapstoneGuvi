package org.example.identityservice.service;


import org.example.identityservice.entity.User;
import org.example.identityservice.exception.UserNotFoundException;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    private User createUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email(email)
                .password("encoded-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .build();
    }

    @Test
    void shouldGetUserById() {
        User user = createUser(1L, "test@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userService.getUserById(1L))
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenUserIdDoesNotExist() {
        when(userRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(99L))
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository).findById(99L);
    }

    @Test
    void shouldGetUserByEmail() {
        User user = createUser(1L, "test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(
                        userService.getUserByEmail("test@example.com")
                )
                .assertNext(response -> assertNotNull(response))
                .verifyComplete();

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void shouldThrowWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        userService.getUserByEmail("missing@example.com")
                )
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    void shouldGetAllUsers() {
        User first = createUser(1L, "first@example.com");
        User second = createUser(2L, "second@example.com");

        when(userRepository.findAll())
                .thenReturn(Flux.just(first, second));

        StepVerifier.create(userService.getAllUsers())
                .expectNextCount(2)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(userService.getAllUsers())
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void shouldPropagateRepositoryErrorWhenGettingUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Mono.error(
                        new RuntimeException("Database error")
                ));

        StepVerifier.create(userService.getUserById(1L))
                .expectErrorMessage("Database error")
                .verify();
    }

    @Test
    void shouldPropagateRepositoryErrorWhenGettingAllUsers() {
        when(userRepository.findAll())
                .thenReturn(Flux.error(
                        new RuntimeException("Database error")
                ));

        StepVerifier.create(userService.getAllUsers())
                .expectErrorMessage("Database error")
                .verify();
    }
}