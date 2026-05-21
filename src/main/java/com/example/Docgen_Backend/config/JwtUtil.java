package com.example.Docgen_Backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String secret;

    // =========================
    // EXTRACT CLAIMS
    // =========================

    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // EXTRACT USER ID
    // =========================

    public String extractUserId(String token) {

        Claims claims = getClaims(token);

        Object id = claims.get("id");

        return id != null ? id.toString() : null;
    }

    // =========================
    // EXTRACT EMAIL
    // =========================

    public String extractEmail(String token) {

        return getClaims(token)
                .get("email", String.class);
    }

    // =========================
    // EXTRACT ROLE
    // =========================

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    // =========================
    // VALIDATE TOKEN
    // =========================

    public boolean validateToken(String token) {

        try {

            getClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}