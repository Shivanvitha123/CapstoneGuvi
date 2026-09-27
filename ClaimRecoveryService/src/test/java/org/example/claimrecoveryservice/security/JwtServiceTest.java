package org.example.claimrecoveryservice.security;


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
            "RiskTwinNexusJwtSecretKeyForDevelopmentOnly123456789";

    private JwtService jwtService;

    private SecretKey key;

    @BeforeEach
    void setUp() {

        jwtService =
                new JwtService(SECRET);

        key =
                Keys.hmacShaKeyFor(
                        SECRET.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
    }

    private String token() {

        return Jwts.builder()
                .subject("100")
                .claim("role", "CLAIMS_ADJUSTER")
                .claim(
                        "email",
                        "claims@risktwin.com"
                )
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 3600000
                        )
                )
                .signWith(key)
                .compact();
    }

    @Test
    void validToken() {

        assertTrue(
                jwtService.isValidToken(
                        token()
                )
        );
    }

    @Test
    void extractUserId() {

        assertEquals(
                100L,
                jwtService.extractUserId(
                        token()
                )
        );
    }

    @Test
    void extractRole() {

        assertEquals(
                "CLAIMS_ADJUSTER",
                jwtService.extractRole(
                        token()
                )
        );
    }

    @Test
    void extractEmail() {

        assertEquals(
                "claims@risktwin.com",
                jwtService.extractEmail(
                        token()
                )
        );
    }

    @Test
    void invalidToken() {

        assertFalse(
                jwtService.isValidToken(
                        "invalid.token"
                )
        );
    }
}


