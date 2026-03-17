# ADR-0002: Multi-Tenancy Strategy

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

SubscriptIO is a B2B SaaS platform where multiple organizations manage their own subscriptions. Each tenant's data must be isolated. The approach needs to be straightforward to implement while still demonstrating real multi-tenancy patterns that show up in production systems.

## Decision

Use shared-schema multi-tenancy with a `tenant_id` column on every tenant-scoped table. A Hibernate filter automatically applies the tenant context to all queries, so application code never manually filters by tenant.

## Consequences

- **POS-001:** Single database and single schema keeps infrastructure simple.
- **POS-002:** Demonstrates a pattern used widely in real SaaS products (Slack, Notion, etc.).
- **POS-003:** Hibernate's `@Filter` handles tenant scoping transparently — less room for developer error.
- **POS-004:** Easy to seed demo data for multiple tenants without extra databases.
- **NEG-001:** A missing `tenant_id` filter means data leaks across tenants. Must test this carefully.
- **NEG-002:** Noisy-neighbor risk — one tenant's heavy queries can affect others.
- **NEG-003:** Tenant-specific migrations or schema changes are not possible.

## Alternatives Considered

- **ALT-001: Schema-per-tenant** — Each tenant gets its own Postgres schema. Better isolation, but complicates migrations and connection management. Hard to justify for a portfolio project.
- **ALT-002: Database-per-tenant** — Full isolation with separate databases. Best security, but operational overhead is high. More suited to enterprise/compliance-heavy products.

## Implementation Notes

- **IMP-001:** `TenantContext` uses a `ThreadLocal` set by a servlet filter on every request.
- **IMP-002:** All entity base classes extend `TenantAwareEntity` which includes the `tenant_id` column.
- **IMP-003:** Integration tests verify that Tenant A cannot read Tenant B's data.
- **IMP-004:** Unique constraints are compound: `(tenant_id, natural_key)` instead of just `(natural_key)`.
