package com.subscriptio.api.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role
) {
}
