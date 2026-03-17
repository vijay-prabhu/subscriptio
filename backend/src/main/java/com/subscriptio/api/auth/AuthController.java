package com.subscriptio.api.auth;

import com.subscriptio.api.dto.request.LoginRequest;
import com.subscriptio.api.dto.response.AuthResponse;
import com.subscriptio.infrastructure.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // For demo: accept any login with password "demo" and assign to tenant 1
        // In production: validate against user store
        if (!"demo".equals(request.password())) {
            return ResponseEntity.status(401).build();
        }

        String accessToken = tokenProvider.generateAccessToken(request.email(), 1L, "ADMIN");
        String refreshToken = tokenProvider.generateRefreshToken(request.email());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, request.email(), "ADMIN"));
    }
}
