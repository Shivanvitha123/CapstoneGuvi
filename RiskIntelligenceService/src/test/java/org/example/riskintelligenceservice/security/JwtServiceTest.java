package org.example.riskintelligenceservice.security;


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

    private JwtService jwtService;

    private static final String SECRET =
            "RiskTwinNexusJwtSecretKeyForDevelopmentOnly123456789";

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(SECRET);

        secretKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String createToken() {

        return Jwts.builder()
                .subject("100")
                .claim("role", "RISK_ENGINEER")
                .claim("email", "risk@risktwin.com")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis() + 3600000
                        )
                )
                .signWith(secretKey)
                .compact();
    }

    @Test
    void validTokenShouldReturnTrue() {

        String token = createToken();

        assertTrue(
                jwtService.isValidToken(token)
        );
    }

    @Test
    void shouldExtractUserId() {

        String token = createToken();

        assertEquals(
                100L,
                jwtService.extractUserId(token)
        );
    }

    @Test
    void shouldExtractRole() {

        String token = createToken();

        assertEquals(
                "RISK_ENGINEER",
                jwtService.extractRole(token)
        );
    }

    @Test
    void shouldExtractEmail() {

        String token = createToken();

        assertEquals(
                "risk@risktwin.com",
                jwtService.extractEmail(token)
        );
    }

    @Test
    void invalidTokenShouldReturnFalse() {

        assertFalse(
                jwtService.isValidToken(
                        "invalid.jwt.token"
                )
        );
    }

    @Test
    void expiredTokenShouldReturnFalse() {

        String token =
                Jwts.builder()
                        .subject("100")
                        .claim("role", "RISK_ENGINEER")
                        .claim(
                                "email",
                                "risk@risktwin.com"
                        )
                        .issuedAt(
                                new Date(
                                        System.currentTimeMillis()
                                                - 7200000
                                )
                        )
                        .expiration(
                                new Date(
                                        System.currentTimeMillis()
                                                - 3600000
                                )
                        )
                        .signWith(secretKey)
                        .compact();

        assertFalse(
                jwtService.isValidToken(token)
        );
    }
}

