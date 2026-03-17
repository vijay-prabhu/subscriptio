# ADR-0006: Stripe Integration Pattern

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

SubscriptIO uses Stripe for payment processing. The Stripe SDK is a third-party dependency that shouldn't leak into domain logic. Stripe also sends webhooks for async events (payment succeeded, payment failed, dispute created). These webhooks can arrive more than once, so the system must handle duplicates without side effects.

## Decision

Isolate Stripe behind a port/adapter boundary. The domain defines a `PaymentGateway` port. The infrastructure layer implements it with a `StripePaymentAdapter` that wraps the Stripe SDK. Webhook processing uses a database-backed idempotency log — each webhook event ID is stored, and duplicates are skipped.

## Consequences

- **POS-001:** Domain code never imports Stripe classes. Swapping to another provider means writing a new adapter.
- **POS-002:** Unit tests mock `PaymentGateway` instead of dealing with Stripe API stubs.
- **POS-003:** Webhook idempotency prevents duplicate invoice creation or double state transitions.
- **POS-004:** The idempotency log doubles as an audit trail of all Stripe events received.
- **NEG-001:** Mapping between Stripe's data model and the domain model adds boilerplate.
- **NEG-002:** Webhook signature verification is adapter-specific logic that needs its own tests.
- **NEG-003:** Must keep the Stripe SDK version in sync with Stripe's API version.

## Alternatives Considered

- **ALT-001: Direct Stripe SDK calls in services** — Less code, but Stripe types leak everywhere. Testing requires Stripe's test mode or complex mocking.
- **ALT-002: Message queue for webhooks** — Push webhooks to a queue (SQS, RabbitMQ) and process async. Better resilience, but adds infrastructure and makes local dev harder.

## Implementation Notes

- **IMP-001:** `PaymentGateway` port defines `createCharge()`, `createCustomer()`, `refund()`.
- **IMP-002:** `StripePaymentAdapter` maps between domain types and Stripe SDK types.
- **IMP-003:** `WebhookEventLog` table stores `(event_id, event_type, received_at, processed)`.
- **IMP-004:** Webhook controller verifies Stripe signature before processing.
