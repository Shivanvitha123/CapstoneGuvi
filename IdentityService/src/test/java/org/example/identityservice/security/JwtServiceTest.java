package org.example.identityservice.security;

import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789012345678901234567890123456789012345678901234567890123";

    private JwtService createService(long expiration) {
        return new JwtService(SECRET, expiration);
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(Role.ADMIN)
                .active(true)
                .build();
    }

    @Test
    void shouldGenerateValidToken() {
        JwtService service = createService(3600000);

        String token = service.generateToken(createUser());

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(service.validateToken(token).getSubject().equals("1"));
    }

    @Test
    void shouldExtractUserId() {
        JwtService service = createService(3600000);

        String token = service.generateToken(createUser());

        assertEquals(1L, service.extractUserId(token));
    }

    @Test
    void shouldExtractRole() {
        JwtService service = createService(3600000);

        String token = service.generateToken(createUser());

        assertEquals("ADMIN", service.extractRole(token));
    }

    @Test
    void shouldIncludeEmailInClaims() {
        JwtService service = createService(3600000);

        String token = service.generateToken(createUser());

        assertEquals(
                "test@example.com",
                service.validateToken(token).get("email", String.class)
        );
    }

    @Test
    void shouldRejectInvalidToken() {
        JwtService service = createService(3600000);

        assertThrows(
                Exception.class,
                () -> service.validateToken("invalid-token")
        );
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {
        JwtService service1 = createService(3600000);
        JwtService service2 = new JwtService(
                "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                3600000
        );

        String token = service1.generateToken(createUser());

        assertThrows(
                Exception.class,
                () -> service2.validateToken(token)
        );
    }

    @Test
    void shouldRejectShortSecret() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("short-secret", 3600000)
        );
    }

    @Test
    void shouldRejectNullSecret() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(null, 3600000)
        );
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        JwtService service = createService(-1000);

        String token = service.generateToken(createUser());

        Thread.sleep(10);

        assertThrows(
                Exception.class,
                () -> service.validateToken(token)
        );
    }
}