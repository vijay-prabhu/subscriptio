package com.subscriptio.domain.service;

import com.subscriptio.domain.model.Subscription;
import com.subscriptio.domain.model.SubscriptionStatus;

import java.util.Map;
import java.util.Set;

public final class SubscriptionStateMachine {

    private static final Map<SubscriptionStatus, Set<SubscriptionStatus>> VALID_TRANSITIONS = Map.of(
            SubscriptionStatus.TRIALING, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED),
            SubscriptionStatus.ACTIVE, Set.of(SubscriptionStatus.PAST_DUE, SubscriptionStatus.CANCELLED),
            SubscriptionStatus.PAST_DUE, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED),
            SubscriptionStatus.CANCELLED, Set.of(SubscriptionStatus.EXPIRED),
            SubscriptionStatus.EXPIRED, Set.of()
    );

    private SubscriptionStateMachine() {
    }

    public static void transition(Subscription subscription, SubscriptionStatus target) {
        SubscriptionStatus current = subscription.getStatus();
        Set<SubscriptionStatus> allowed = VALID_TRANSITIONS.getOrDefault(current, Set.of());

        if (!allowed.contains(target)) {
            throw new IllegalStateException(
                    "Invalid subscription transition from " + current + " to " + target);
        }
    }

    public static boolean canTransition(SubscriptionStatus from, SubscriptionStatus to) {
        return VALID_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
    }
}
