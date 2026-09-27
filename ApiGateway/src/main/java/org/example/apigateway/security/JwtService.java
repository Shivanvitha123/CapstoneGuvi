package org.example.apigateway.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {

        try {

            Claims claims = extractClaims(token);

            Date expiration = claims.getExpiration();

            return claims.getSubject() != null
                    && expiration != null
                    && expiration.after(new Date());

        } catch (Exception exception) {

            return false;
        }
    }

    public String extractUserId(String token) {

        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }
}

