package com.company.employees.application.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds
) {
}
