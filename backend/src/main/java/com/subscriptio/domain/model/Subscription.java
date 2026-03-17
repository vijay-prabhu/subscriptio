package com.subscriptio.domain.model;

import com.subscriptio.domain.service.SubscriptionStateMachine;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.TRIALING;

    @Column(name = "current_period_start", nullable = false)
    private Instant currentPeriodStart;

    @Column(name = "current_period_end", nullable = false)
    private Instant currentPeriodEnd;

    @Column(name = "trial_end")
    private Instant trialEnd;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancel_at_period_end", nullable = false)
    private boolean cancelAtPeriodEnd = false;

    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;

    protected Subscription() {
    }

    public Subscription(Tenant tenant, Customer customer, Plan plan,
                        Instant currentPeriodStart, Instant currentPeriodEnd) {
        this.tenant = tenant;
        this.customer = customer;
        this.plan = plan;
        this.currentPeriodStart = currentPeriodStart;
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public void activate() {
        SubscriptionStateMachine.transition(this, SubscriptionStatus.ACTIVE);
        this.status = SubscriptionStatus.ACTIVE;
    }

    public void markPastDue() {
        SubscriptionStateMachine.transition(this, SubscriptionStatus.PAST_DUE);
        this.status = SubscriptionStatus.PAST_DUE;
    }

    public void cancel() {
        SubscriptionStateMachine.transition(this, SubscriptionStatus.CANCELLED);
        this.status = SubscriptionStatus.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    public void expire() {
        SubscriptionStateMachine.transition(this, SubscriptionStatus.EXPIRED);
        this.status = SubscriptionStatus.EXPIRED;
    }

    public void requestCancelAtPeriodEnd() {
        this.cancelAtPeriodEnd = true;
    }

    public void advanceBillingPeriod(Instant newPeriodStart, Instant newPeriodEnd) {
        this.currentPeriodStart = newPeriodStart;
        this.currentPeriodEnd = newPeriodEnd;
    }

    public void changePlan(Plan newPlan) {
        this.plan = newPlan;
    }

    // Getters

    public Tenant getTenant() {
        return tenant;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Plan getPlan() {
        return plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public Instant getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public Instant getTrialEnd() {
        return trialEnd;
    }

    public void setTrialEnd(Instant trialEnd) {
        this.trialEnd = trialEnd;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public boolean isCancelAtPeriodEnd() {
        return cancelAtPeriodEnd;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public boolean isDue() {
        return (status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIALING)
                && currentPeriodEnd.isBefore(Instant.now());
    }
}
