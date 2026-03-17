package com.subscriptio.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckoutRequest(
        @NotNull UUID tenantId,
        @NotNull UUID customerId,
        @NotNull UUID planId,
        String successUrl,
        String cancelUrl
) {
}
