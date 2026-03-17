package com.subscriptio.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangePlanRequest(
        @NotNull UUID newPlanId
) {
}
