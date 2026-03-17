package com.subscriptio.api.dto.response;

import com.subscriptio.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        String status,
        PlanResponse plan,
        UUID customerId,
        Instant currentPeriodStart,
        Instant currentPeriodEnd,
        Instant trialEnd,
        boolean cancelAtPeriodEnd,
        Instant cancelledAt,
        Instant createdAt
) {
    public static SubscriptionResponse from(Subscription sub) {
        return new SubscriptionResponse(
                sub.getExternalId(),
                sub.getStatus().name(),
                PlanResponse.from(sub.getPlan()),
                sub.getCustomer().getExternalId(),
                sub.getCurrentPeriodStart(),
                sub.getCurrentPeriodEnd(),
                sub.getTrialEnd(),
                sub.isCancelAtPeriodEnd(),
                sub.getCancelledAt(),
                sub.getCreatedAt()
        );
    }
}
