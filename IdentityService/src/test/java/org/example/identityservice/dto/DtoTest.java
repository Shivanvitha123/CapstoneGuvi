package org.example.identityservice.dto;

import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void shouldCreateAuthResponse() {
        AuthResponse response = new AuthResponse(
                "test-token",
                1L,
                "Test User",
                "test@example.com",
                Role.ADMIN
        );

        assertEquals("test-token", response.token());
        assertEquals(1L, response.userId());
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals(Role.ADMIN, response.role());
    }

    @Test
    void shouldCreateLoginRequest() {
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password123"
        );

        assertEquals("test@example.com", request.email());
        assertEquals("password123", request.password());
    }

    @Test
    void shouldCreateRegisterRequest() {
        RegisterRequest request = new RegisterRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        assertEquals("Test User", request.name());
        assertEquals("test@example.com", request.email());
        assertEquals("password123", request.password());
    }

    @Test
    void shouldCreateUserResponse() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@example.com",
                Role.BUSINESS_OWNER,
                true,
                now,
                now
        );

        assertEquals(1L, response.id());
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals(Role.BUSINESS_OWNER, response.role());
        assertTrue(response.active());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }

    @Test
    void shouldConvertUserToUserResponse() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .password("encoded-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponse response = UserResponse.from(user);

        assertEquals(user.getId(), response.id());
        assertEquals(user.getName(), response.name());
        assertEquals(user.getEmail(), response.email());
        assertEquals(user.getRole(), response.role());
        assertEquals(user.getActive(), response.active());
        assertEquals(user.getCreatedAt(), response.createdAt());
        assertEquals(user.getUpdatedAt(), response.updatedAt());
    }
}