package com.company.employees.application.service;

import com.company.employees.application.dto.request.LoginRequest;
import com.company.employees.application.dto.response.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class AuthenticationService {

    private static final long TOKEN_LIFETIME_SECONDS = 3600;

    private final String adminUsername;
    private final String adminPassword;
    private final SecretKey signingKey;

    public AuthenticationService(
            @ConfigProperty(name = "app.admin.username", defaultValue = "admin") String adminUsername,
            @ConfigProperty(name = "app.admin.password", defaultValue = "") String adminPassword,
            @ConfigProperty(name = "jwt.signing-secret", defaultValue = "") String signingSecret
    ) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.signingKey = signingSecret.trim().length() >= 32
                ? Keys.hmacShaKeyFor(signingSecret.getBytes(StandardCharsets.UTF_8))
                : null;
    }

    public LoginResponse login(LoginRequest request) {
        if (signingKey == null) {
            throw new IllegalStateException("JWT signing secret is not configured.");
        }
        if (!adminUsername.equals(request.username())
                || adminPassword.isBlank()
                || !adminPassword.equals(request.password())) {
            return null;
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(TOKEN_LIFETIME_SECONDS);
        String token = Jwts.builder()
                .subject(request.username())
                .claim("role", "admin")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .issuer("employee-management")
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
        return new LoginResponse(token, "Bearer", TOKEN_LIFETIME_SECONDS);
    }
}
