# ADR-0003: Hexagonal Architecture

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

The backend needs a clear separation between business logic and framework code. Subscription billing has enough domain complexity that it deserves a clean domain model — not one polluted with JPA annotations and Spring dependencies. The architecture should also make the codebase easy to test without spinning up Spring context.

## Decision

Use hexagonal architecture (ports and adapters). The domain core contains entities, value objects, and domain services with zero Spring or JPA imports. Ports are interfaces defined in the domain layer. Adapters in the infrastructure layer implement those ports using Spring, JPA, Stripe SDK, etc.

```
domain/
  model/          # Entities, value objects, enums
  port/in/        # Use case interfaces (driven by API)
  port/out/       # Repository and gateway interfaces (driven by domain)
  service/        # Domain services implementing use cases
infrastructure/
  adapter/in/     # REST controllers
  adapter/out/    # JPA repos, Stripe client, email sender
  config/         # Spring config, security, filters
```

## Consequences

- **POS-001:** Domain logic is testable with plain JUnit — no Spring context needed.
- **POS-002:** Swapping Stripe for another payment provider means writing a new adapter, not changing domain code.
- **POS-003:** Forces clear thinking about what belongs in the domain vs. what's infrastructure.
- **NEG-001:** More files and packages than a traditional layered approach.
- **NEG-002:** Developers unfamiliar with the pattern need onboarding time.
- **NEG-003:** Some pragmatic shortcuts (like using JPA annotations on domain entities) are intentionally avoided, adding boilerplate mapping code.

## Alternatives Considered

- **ALT-001: Traditional layered (Controller → Service → Repository)** — Simpler, but domain logic tends to leak into controllers and services end up as transaction scripts. Doesn't showcase architectural thinking.
- **ALT-002: CQRS with separate read/write models** — Powerful for complex domains, but adds significant complexity. Not justified for this scope.

## Implementation Notes

- **IMP-001:** MapStruct handles mapping between domain entities and JPA entities.
- **IMP-002:** Domain services are plain classes annotated only with `@Service` for Spring discovery — no other Spring imports.
- **IMP-003:** Integration tests use the real adapters. Unit tests mock the ports.
