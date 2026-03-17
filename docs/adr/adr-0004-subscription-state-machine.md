# ADR-0004: Subscription State Machine

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

Subscriptions move through lifecycle states: trialing, active, past due, cancelled, expired. Invalid transitions (like going from expired to trialing) must be impossible. The state rules are core business logic and should be enforced in the domain, not scattered across service methods or left to the database.

## Decision

Model subscription lifecycle as a domain-enforced state machine. The `Subscription` entity holds a `SubscriptionStatus` enum and exposes transition methods (`activate()`, `markPastDue()`, `cancel()`, `expire()`). Each method checks the current state and throws a domain exception if the transition is invalid.

```
Valid transitions:
  TRIALING  → ACTIVE, CANCELLED
  ACTIVE    → PAST_DUE, CANCELLED
  PAST_DUE  → ACTIVE, CANCELLED, EXPIRED
  CANCELLED → (terminal)
  EXPIRED   → (terminal)
```

## Consequences

- **POS-001:** Invalid state transitions fail fast with clear domain exceptions.
- **POS-002:** Transition rules are self-documenting — read the enum and the entity methods.
- **POS-003:** Easy to unit test every valid and invalid transition path.
- **POS-004:** Domain events can be emitted on each transition for audit and analytics.
- **NEG-001:** Adding a new state requires updating the enum, the entity, and all transition tests.
- **NEG-002:** Complex transition logic (like retry counts before expiry) lives in the entity, which can grow large.

## Alternatives Considered

- **ALT-001: Enum-only with no enforcement** — Just store the status as an enum and let service code manage transitions. Simpler, but nothing prevents invalid states. Bugs show up as corrupt data.
- **ALT-002: External workflow engine (e.g., Temporal, Camunda)** — Full workflow orchestration. Way too heavy for this use case.
- **ALT-003: Spring State Machine library** — Framework-provided state machine. Adds a dependency and moves logic out of the domain, which conflicts with the hexagonal approach.

## Implementation Notes

- **IMP-001:** `Subscription.activate()` checks `status == TRIALING` and sets status to `ACTIVE`.
- **IMP-002:** Each transition method registers a `DomainEvent` for downstream processing.
- **IMP-003:** Test class `SubscriptionStateTransitionTest` covers every valid path and every invalid path.
