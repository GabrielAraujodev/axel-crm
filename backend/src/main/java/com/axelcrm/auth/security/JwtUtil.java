package com.axelcrm.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.entity.enums.Role;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String secret;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withClaim("userId", user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .withClaim("organizationId", user.getOrganization().getId().toString())
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public UUID extractUserId(DecodedJWT jwt) {
        return UUID.fromString(jwt.getClaim("userId").asString());
    }

    public String extractEmail(DecodedJWT jwt) {
        return jwt.getClaim("email").asString();
    }

    public Role extractRole(DecodedJWT jwt) {
        return Role.valueOf(jwt.getClaim("role").asString());
    }

    public UUID extractOrganizationId(DecodedJWT jwt) {
        return UUID.fromString(jwt.getClaim("organizationId").asString());
    }

    public long getExpirationInSeconds() {
        return expirationMs / 1000;
    }
}
