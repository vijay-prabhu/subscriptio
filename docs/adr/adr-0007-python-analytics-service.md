# ADR-0007: Python Analytics Service

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

The project needs an analytics component for subscription metrics: MRR, churn rate, cohort retention, revenue forecasts. Building this in Python with FastAPI serves two purposes. First, Python's data ecosystem (pandas, numpy) is better suited for analytical workloads. Second, it demonstrates polyglot architecture — a real-world pattern where teams pick the right language for the job.

## Decision

Build a separate FastAPI microservice for analytics. It reads from the same Postgres database (read replica in production) and exposes REST endpoints for dashboards. The service uses pandas for data transformation and aggregation. It communicates with the Spring Boot backend only through the shared database — no synchronous API calls between services.

## Consequences

- **POS-001:** Shows polyglot architecture — Java backend, Python analytics, both in one project.
- **POS-002:** Python's data libraries make analytical queries and transformations simpler to write.
- **POS-003:** Analytics service scales independently from the main API.
- **POS-004:** Demonstrates cross-language skills relevant to job descriptions requiring both Java and Python.
- **NEG-001:** Two runtimes to manage in CI, Docker, and local dev.
- **NEG-002:** Shared database coupling — schema changes in the backend can break analytics queries.
- **NEG-003:** No compile-time checks on SQL queries in Python (unlike JPA in Java).

## Alternatives Considered

- **ALT-001: All analytics in Spring Boot** — Keep everything in Java. Simpler deployment, but misses the polyglot story and makes analytical code more verbose.
- **ALT-002: Jupyter notebooks** — Great for exploration, but not deployable as a service. Doesn't demonstrate production Python patterns.
- **ALT-003: Dedicated analytics DB (data warehouse)** — ETL into a warehouse like Redshift. Realistic for large scale, but overkill here.

## Implementation Notes

- **IMP-001:** FastAPI app lives in `analytics/` with its own `Dockerfile` and `requirements.txt`.
- **IMP-002:** SQLAlchemy for database access with read-only connection credentials.
- **IMP-003:** Endpoints: `/metrics/mrr`, `/metrics/churn`, `/metrics/cohort-retention`.
- **IMP-004:** Shared `docker-compose.yml` spins up both services pointing at the same Postgres instance.
