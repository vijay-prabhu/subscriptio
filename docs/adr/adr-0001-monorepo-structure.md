# ADR-0001: Monorepo Structure

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

SubscriptIO has multiple components: a Spring Boot backend, an admin dashboard, a customer portal, a Python analytics service, and infrastructure configs. The project serves as a portfolio piece, so reviewers need to see the full system in one place. Cross-service changes (like adding a new subscription field) touch multiple components at once.

## Decision

Use a single monorepo for all services, infrastructure, and documentation.

```
subscriptio/
  backend/          # Spring Boot API
  admin-dashboard/  # React admin UI
  customer-portal/  # React customer UI
  analytics/        # FastAPI analytics service
  infra/            # Terraform, Docker, K8s manifests
  docs/             # ADRs, API specs
```

## Consequences

- **POS-001:** Single clone gives reviewers the full picture of the system.
- **POS-002:** Cross-service refactors land in one commit and one PR.
- **POS-003:** Shared CI pipeline catches integration issues early.
- **POS-004:** Simpler dependency management for shared configs (Docker Compose, env files).
- **NEG-001:** Repo size grows over time as all services live together.
- **NEG-002:** CI runs may take longer without path-based filtering.
- **NEG-003:** Teams in a real org might prefer independent deploy cycles per service.

## Alternatives Considered

- **ALT-001: Polyrepo** — One repo per service. Better isolation and independent CI, but splits the portfolio story across multiple repos. Harder for reviewers to navigate.
- **ALT-002: Monorepo with Nx/Turborepo** — Adds build orchestration tooling. Overkill for a project this size.

## Implementation Notes

- **IMP-001:** Use path-based CI triggers so backend changes don't rebuild the frontend.
- **IMP-002:** Each service has its own Dockerfile and can be built independently.
- **IMP-003:** Root docker-compose.yml orchestrates the full local dev environment.
