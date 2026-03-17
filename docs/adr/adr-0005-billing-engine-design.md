# ADR-0005: Billing Engine Design

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

Subscriptions need daily processing: generate invoices on renewal dates, retry failed payments, expire subscriptions that exceed the retry window. The billing engine is the heartbeat of the system. It needs to be reliable and easy to reason about, but doesn't need enterprise-grade job scheduling for a project of this scale.

## Decision

Use Spring's `@Scheduled` annotation to run a daily billing cron job. The job queries all subscriptions due for processing, generates invoices, triggers payment through the Stripe port, and updates subscription states based on payment results. Each run is idempotent — rerunning the same day produces no duplicate invoices.

## Consequences

- **POS-001:** Zero additional dependencies — `@Scheduled` ships with Spring Boot.
- **POS-002:** Idempotent design means the job is safe to retry on failure.
- **POS-003:** Easy to test by calling the method directly in integration tests.
- **POS-004:** Simple to monitor — log the count of processed subscriptions each run.
- **NEG-001:** Single-node only. In a multi-instance deployment, needs a distributed lock (ShedLock) to prevent duplicate runs.
- **NEG-002:** No built-in retry with backoff — must implement retry logic manually.
- **NEG-003:** No dashboard or job history out of the box.

## Alternatives Considered

- **ALT-001: Quartz Scheduler** — Full-featured job scheduler with persistence, clustering, and misfire handling. More power than needed here, and adds significant configuration overhead.
- **ALT-002: Event-driven billing** — Emit events on subscription creation and let event handlers schedule future billing. Elegant but harder to debug and reason about timing.
- **ALT-003: External cron (Kubernetes CronJob)** — Keeps the app stateless, but adds infra complexity and makes local development harder.

## Implementation Notes

- **IMP-001:** `BillingScheduler` runs at midnight UTC daily via `@Scheduled(cron = "0 0 0 * * *")`.
- **IMP-002:** Idempotency check: skip subscriptions that already have an invoice for the current billing period.
- **IMP-003:** ShedLock integration prevents duplicate runs in multi-instance deployments.
- **IMP-004:** Each billing run logs a summary: subscriptions processed, invoices created, payments failed.
