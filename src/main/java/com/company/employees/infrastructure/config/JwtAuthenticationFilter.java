package com.company.employees.infrastructure.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private final SecretKey signingKey;

    public JwtAuthenticationFilter(
            @ConfigProperty(name = "jwt.signing-secret", defaultValue = "") String signingSecret
    ) {
        signingKey = signingSecret.trim().length() >= 32
                ? Keys.hmacShaKeyFor(signingSecret.getBytes(StandardCharsets.UTF_8))
                : null;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        String path = request.getUriInfo().getPath();
        if (path.startsWith("auth/") || path.startsWith("/auth/")) {
            return;
        }
        if (request.getUriInfo().getPath().startsWith("q/")) {
            return;
        }

        String authorization = request.getHeaderString("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            abort(request, "Missing or invalid Authorization header.");
            return;
        }

        if (signingKey == null) {
            abort(request, "JWT authentication is not configured.");
            return;
        }

        try {
            var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer("employee-management")
                    .build()
                    .parseSignedClaims(authorization.substring(7))
                    .getPayload();
            if (!"admin".equals(claims.get("role", String.class))) {
                abort(request, "Insufficient permissions.");
            }
        } catch (JwtException | IllegalArgumentException exception) {
            abort(request, "Invalid or expired token.");
        }
    }

    private void abort(ContainerRequestContext request, String message) {
        request.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(java.util.Map.of("message", message))
                .build());
    }
}
