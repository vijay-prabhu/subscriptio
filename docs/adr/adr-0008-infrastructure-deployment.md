# ADR-0008: Infrastructure and Deployment

**Status:** Accepted
**Date:** 2026-03-17
**Deciders:** Vijay

## Context

The project needs deployable infrastructure configs to show it's not just a local-only demo. The target audience includes hiring teams that value cloud and DevOps experience. The infra should be realistic but not require a running AWS account to understand. ECS Fargate is a common choice for container workloads without Kubernetes overhead. Kubernetes manifests are included as an alternative for teams that run K8s.

## Decision

Use ECS Fargate as the primary deployment target with Terraform for infrastructure-as-code. Include Kubernetes manifests as a portable alternative. Both options are documented and ready to deploy. GitHub Actions handles CI/CD with separate pipelines per service.

## Consequences

- **POS-001:** Terraform modules demonstrate IaC skills — VPC, ECS, RDS, ALB all defined as code.
- **POS-002:** Fargate eliminates server management — just push containers.
- **POS-003:** K8s manifests show portability and familiarity with both ecosystems.
- **POS-004:** GitHub Actions CI is free for public repos and easy for reviewers to inspect.
- **NEG-001:** Two deployment targets (ECS and K8s) means maintaining two sets of configs.
- **NEG-002:** Fargate costs more per unit compute than EC2 or self-managed K8s nodes.
- **NEG-003:** Terraform state management needs an S3 backend for team use.

## Alternatives Considered

- **ALT-001: EKS only** — Managed Kubernetes on AWS. More portable than Fargate, but EKS has higher operational complexity and cluster costs.
- **ALT-002: Bare EC2 with Docker Compose** — Simplest deployment, but doesn't demonstrate orchestration skills.
- **ALT-003: Serverless (Lambda)** — Interesting for some workloads, but doesn't fit a long-running subscription billing engine.

## Implementation Notes

- **IMP-001:** Terraform lives in `infra/terraform/` with modules for networking, compute, database, and load balancing.
- **IMP-002:** K8s manifests live in `infra/k8s/` with deployments, services, and config maps.
- **IMP-003:** GitHub Actions workflows in `.github/workflows/` — one per service, triggered on path changes.
- **IMP-004:** Environment configs (staging, production) use Terraform workspaces.
