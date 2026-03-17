package com.subscriptio.domain.model;

import com.subscriptio.domain.service.SubscriptionStateMachine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionStateMachineTest {

    @Nested
    @DisplayName("Valid transitions")
    class ValidTransitions {

        @Test
        void trialingToActive() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.TRIALING, SubscriptionStatus.ACTIVE)).isTrue();
        }

        @Test
        void trialingToCancelled() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.TRIALING, SubscriptionStatus.CANCELLED)).isTrue();
        }

        @Test
        void activeToPastDue() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE)).isTrue();
        }

        @Test
        void activeToCancelled() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED)).isTrue();
        }

        @Test
        void pastDueToActive() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.PAST_DUE, SubscriptionStatus.ACTIVE)).isTrue();
        }

        @Test
        void pastDueToCancelled() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.PAST_DUE, SubscriptionStatus.CANCELLED)).isTrue();
        }

        @Test
        void cancelledToExpired() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.CANCELLED, SubscriptionStatus.EXPIRED)).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid transitions")
    class InvalidTransitions {

        @Test
        void trialingToPastDue() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.TRIALING, SubscriptionStatus.PAST_DUE)).isFalse();
        }

        @Test
        void trialingToExpired() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.TRIALING, SubscriptionStatus.EXPIRED)).isFalse();
        }

        @Test
        void activeToTrialing() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING)).isFalse();
        }

        @Test
        void cancelledToActive() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.CANCELLED, SubscriptionStatus.ACTIVE)).isFalse();
        }

        @Test
        void expiredToAnything() {
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.EXPIRED, SubscriptionStatus.ACTIVE)).isFalse();
            assertThat(SubscriptionStateMachine.canTransition(
                    SubscriptionStatus.EXPIRED, SubscriptionStatus.TRIALING)).isFalse();
        }
    }

    @Nested
    @DisplayName("Transition enforcement on Subscription entity")
    class TransitionEnforcement {

        @Test
        void activateFromTrialing() {
            Subscription sub = createSubscription(SubscriptionStatus.TRIALING);
            sub.activate();
            assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        }

        @Test
        void cancelFromActive() {
            Subscription sub = createSubscription(SubscriptionStatus.ACTIVE);
            sub.cancel();
            assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.CANCELLED);
            assertThat(sub.getCancelledAt()).isNotNull();
        }

        @Test
        void markPastDueFromActive() {
            Subscription sub = createSubscription(SubscriptionStatus.ACTIVE);
            sub.markPastDue();
            assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.PAST_DUE);
        }

        @Test
        void rejectsInvalidTransition() {
            Subscription sub = createSubscription(SubscriptionStatus.EXPIRED);
            assertThatThrownBy(sub::activate)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid subscription transition");
        }
    }

    private Subscription createSubscription(SubscriptionStatus status) {
        Tenant tenant = new Tenant("Test Corp", "test-corp");
        Customer customer = new Customer(tenant, "test@example.com", "Test User");
        Plan plan = new Plan(tenant, "Pro", java.math.BigDecimal.valueOf(49.99), "monthly");
        Subscription sub = new Subscription(tenant, customer, plan,
                java.time.Instant.now(), java.time.Instant.now().plusSeconds(86400 * 30));
        // Force status via reflection for testing different starting states
        try {
            var field = Subscription.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(sub, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sub;
    }
}
