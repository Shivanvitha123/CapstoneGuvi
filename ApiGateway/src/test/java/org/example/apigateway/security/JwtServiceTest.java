package org.example.apigateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789012345678901234567890123456789012345678901234567890123";

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", SECRET);

        key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String token(
            String subject, String role, Date expiration) {

        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis() - 1000))
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Test
    void shouldValidateValidToken() {
        String token = token(
                "user-123",
                "ADMIN",
                new Date(System.currentTimeMillis() + 60_000)
        );

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        String token = token(
                "user-123",
                "ADMIN",
                new Date(System.currentTimeMillis() - 60_000)
        );

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        assertFalse(jwtService.isValid("not-a-jwt"));
    }

    @Test
    void shouldExtractUserId() {
        String token = token(
                "user-123",
                "ADMIN",
                new Date(System.currentTimeMillis() + 60_000)
        );

        assertEquals("user-123", jwtService.extractUserId(token));
    }

    @Test
    void shouldExtractRole() {
        String token = token(
                "user-123",
                "UNDERWRITER",
                new Date(System.currentTimeMillis() + 60_000)
        );

        assertEquals("UNDERWRITER", jwtService.extractRole(token));
    }

    @Test
    void shouldExtractClaims() {
        String token = token(
                "user-123",
                "ADMIN",
                new Date(System.currentTimeMillis() + 60_000)
        );

        var claims = jwtService.extractClaims(token);

        assertEquals("user-123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void shouldRejectTokenSignedWithDifferentKey() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user-123")
                .claim("role", "ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey)
                .compact();

        assertFalse(jwtService.isValid(token));
    }
}