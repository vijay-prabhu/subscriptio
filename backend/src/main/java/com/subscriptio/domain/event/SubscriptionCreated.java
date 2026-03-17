package com.subscriptio.domain.event;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionCreated(
        UUID subscriptionId,
        UUID customerId,
        UUID planId,
        Instant occurredAt
) implements DomainEvent {

    public SubscriptionCreated(UUID subscriptionId, UUID customerId, UUID planId) {
        this(subscriptionId, customerId, planId, Instant.now());
    }
}
