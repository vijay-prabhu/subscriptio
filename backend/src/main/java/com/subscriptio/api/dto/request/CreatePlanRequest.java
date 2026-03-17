package com.subscriptio.api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreatePlanRequest(
        @NotNull UUID tenantId,
        @NotBlank @Size(max = 100) String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @NotBlank String currency,
        @NotBlank String billingInterval,
        @Min(0) int trialDays,
        List<String> features
) {
}
