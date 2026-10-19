
package com.akash.employeemanagementsystem.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET =
            "AkashEmployeeManagementSystemSecretKey2026Secure";

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email) {
        long expirationTime = 1000L * 60 * 60; // 1 hour

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject().equals(email)
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
