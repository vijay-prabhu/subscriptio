package com.subscriptio.domain.event;

import com.subscriptio.domain.model.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionStatusChanged(
        UUID subscriptionId,
        SubscriptionStatus previousStatus,
        SubscriptionStatus newStatus,
        Instant occurredAt
) implements DomainEvent {

    public SubscriptionStatusChanged(UUID subscriptionId, SubscriptionStatus previousStatus, SubscriptionStatus newStatus) {
        this(subscriptionId, previousStatus, newStatus, Instant.now());
    }
}
