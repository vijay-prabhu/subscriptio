# Subscription State Machine

```mermaid
stateDiagram-v2
    [*] --> TRIALING : Customer subscribes to plan with trial

    TRIALING --> ACTIVE : Trial ends + payment succeeds
    TRIALING --> CANCELLED : Customer cancels during trial

    ACTIVE --> PAST_DUE : Payment fails
    ACTIVE --> CANCELLED : Customer cancels

    PAST_DUE --> ACTIVE : Retry payment succeeds
    PAST_DUE --> CANCELLED : Max retries exceeded or customer cancels

    CANCELLED --> EXPIRED : Grace period ends

    EXPIRED --> [*]
```

## Transition Rules

All transitions are enforced in `SubscriptionStateMachine.java` in the domain layer. Invalid transitions throw `IllegalStateException`. The entity methods (`activate()`, `cancel()`, `markPastDue()`, `expire()`) call the state machine before updating status.

## Direct-to-Active Flow

If a plan has `trial_days = 0`, the subscription skips `TRIALING` and starts as `ACTIVE` immediately.

```mermaid
stateDiagram-v2
    [*] --> ACTIVE : No trial, payment succeeds
    ACTIVE --> PAST_DUE : Payment fails
    ACTIVE --> CANCELLED : Customer cancels
```
