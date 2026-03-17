package com.subscriptio.api.dto.response;

import com.subscriptio.domain.model.Plan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        String billingInterval,
        int trialDays,
        String features,
        boolean isActive,
        Instant createdAt
) {
    public static PlanResponse from(Plan plan) {
        return new PlanResponse(
                plan.getExternalId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBillingInterval(),
                plan.getTrialDays(),
                plan.getFeatures(),
                plan.isActive(),
                plan.getCreatedAt()
        );
    }
}
