package org.example.businessservice.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "RiskTwinDevelopmentSecretKeyForJWTAuthentication2026";

    private JwtService jwtService;

    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);

        key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldValidateValidToken() {

        String token = createToken(
                "5",
                "BUSINESS_OWNER",
                "owner@abcinsurance.com"
        );

        assertTrue(jwtService.isValid(token));
    }

    @Test
    void shouldExtractUserId() {

        String token = createToken(
                "5",
                "BUSINESS_OWNER",
                "owner@abcinsurance.com"
        );

        assertEquals(
                5L,
                jwtService.extractUserId(token)
        );
    }

    @Test
    void shouldExtractRole() {

        String token = createToken(
                "5",
                "BUSINESS_OWNER",
                "owner@abcinsurance.com"
        );

        assertEquals(
                "BUSINESS_OWNER",
                jwtService.extractRole(token)
        );
    }

    @Test
    void shouldExtractEmail() {

        String token = createToken(
                "5",
                "BUSINESS_OWNER",
                "owner@abcinsurance.com"
        );

        assertEquals(
                "owner@abcinsurance.com",
                jwtService.extractEmail(token)
        );
    }

    @Test
    void shouldRejectInvalidToken() {

        String token = createToken(
                "5",
                "BUSINESS_OWNER",
                "owner@abcinsurance.com"
        );

        String modifiedToken =
                token.substring(0, token.length() - 5)
                        + "XXXXX";

        assertFalse(
                jwtService.isValid(modifiedToken)
        );
    }

    @Test
    void shouldRejectExpiredToken() {

        String token = Jwts.builder()
                .subject("5")
                .claim("role", "BUSINESS_OWNER")
                .claim(
                        "email",
                        "owner@abcinsurance.com"
                )
                .issuedAt(
                        new Date(
                                System.currentTimeMillis() - 7200000
                        )
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis() - 3600000
                        )
                )
                .signWith(key)
                .compact();

        assertFalse(
                jwtService.isValid(token)
        );
    }

    private String createToken(
            String userId,
            String role,
            String email
    ) {

        Date now = new Date();

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .claim("email", email)
                .issuedAt(now)
                .expiration(
                        new Date(
                                now.getTime() + 3600000
                        )
                )
                .signWith(key)
                .compact();
    }
}

